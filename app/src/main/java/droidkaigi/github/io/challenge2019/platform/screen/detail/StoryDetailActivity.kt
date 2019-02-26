package droidkaigi.github.io.challenge2019.platform.screen.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.recyclerview.widget.DividerItemDecoration
import com.wada811.lifecycledisposable.disposeOnLifecycle
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.databinding.StoryDetailActivityBinding
import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.platform.MyApplication
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers

class StoryDetailActivity : AppCompatActivity() {

    private fun <T : ViewDataBinding> ComponentActivity.bind(layoutId: Int): T = DataBindingUtil.setContentView(this, layoutId)
    private val binding by lazy { bind<StoryDetailActivityBinding>(R.layout.story_detail_activity) }
    private lateinit var story: Story
    private val viewModel by lazy {
        ViewModelProvider(this, object : Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return StoryDetailActivityViewModel(MyApplication.Instance.storyService, story) as T
            }
        }).get(StoryDetailActivityViewModel::class.java)
    }
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        story = MyApplication.Instance.storyService.currentStory ?: return finish()
        binding.viewModel = viewModel

        binding.commentRecyclerView.isNestedScrollingEnabled = false
        binding.commentRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        commentAdapter = CommentAdapter(emptyList())
        binding.commentRecyclerView.adapter = commentAdapter

        loadUrlAndComments()
    }

    private fun loadUrlAndComments() {
        val loadUrlSingle = Single.create<Boolean> { emitter ->
            binding.webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    emitter.onSuccess(true)
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    emitter.onError(RuntimeException("Load error"))
                }
            }
        }
        Singles.zip(
            loadUrlSingle,
            viewModel.read()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()),
            viewModel.getComments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
            .doOnSubscribe { viewModel.isLoading.set(true) }
            .doOnSuccess { viewModel.isLoading.set(false) }
            .doOnError { viewModel.isLoading.set(false) }
            .subscribe({
                val (_, _, comments) = it
                commentAdapter.comments = comments
                commentAdapter.notifyDataSetChanged()
            }, {
                MyApplication.Instance.showError(it)
            })
            .disposeOnLifecycle(this, ON_DESTROY)
        binding.webView.loadUrl(story.url)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.refresh -> {
                loadUrlAndComments()
                return true
            }
            R.id.exit -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
