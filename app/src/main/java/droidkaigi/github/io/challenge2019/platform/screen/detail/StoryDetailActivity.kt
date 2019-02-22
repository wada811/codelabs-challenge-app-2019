package droidkaigi.github.io.challenge2019.platform.screen.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.annotation.ContentView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.recyclerview.widget.DividerItemDecoration
import com.squareup.moshi.Types
import com.wada811.lifecycledisposable.disposeOnLifecycle
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.databinding.StoryDetailActivityBinding
import droidkaigi.github.io.challenge2019.domain.Comment
import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.platform.MyApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch

@ContentView(R.layout.story_detail_activity)
class StoryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ITEM_JSON = "droidkaigi.github.io.challenge2019.EXTRA_ITEM_JSON"
        const val READ_ARTICLE_ID = "read_article_id"
        private const val STATE_COMMENTS = "comments"
    }

    private lateinit var commentAdapter: CommentAdapter

    private var hideProgressTask: AsyncTask<Unit, Unit, Unit>? = null
    private val storyJsonAdapter = MyApplication.Instance.moshi.adapter(Story::class.java)
    private val commentsJsonAdapter =
        MyApplication.Instance.moshi.adapter<List<Comment>>(Types.newParameterizedType(List::class.java, Comment::class.java))

    private lateinit var story: Story

    private fun <T : ViewDataBinding> ComponentActivity.bind(): T = DataBindingUtil.bind(window.decorView.findViewById(android.R.id.content))!!
    private val binding by lazy { bind<StoryDetailActivityBinding>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = bind<StoryDetailActivityBinding>()

        story = intent.getStringExtra(EXTRA_ITEM_JSON)?.let {
            storyJsonAdapter.fromJson(it)
        } ?: throw IllegalArgumentException("EXTRA_ITEM_JSON is required.")

        binding.commentRecyclerView.isNestedScrollingEnabled = false
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.commentRecyclerView.addItemDecoration(itemDecoration)
        commentAdapter = CommentAdapter(emptyList())
        binding.commentRecyclerView.adapter = commentAdapter

        title = story.title

        val savedComments = savedInstanceState?.let { bundle ->
            bundle.getString(STATE_COMMENTS)?.let { itemsJson ->
                commentsJsonAdapter.fromJson(itemsJson)
            }
        }

        if (savedComments != null) {
            commentAdapter.comments = savedComments.map { CommentViewModel(it) }
            commentAdapter.notifyDataSetChanged()
            binding.webView.loadUrl(story.url)
            return
        }

        loadUrlAndComments()
    }

    private fun loadUrlAndComments() {
        val progressLatch = CountDownLatch(2)

        hideProgressTask = @SuppressLint("StaticFieldLeak") object : AsyncTask<Unit, Unit, Unit>() {

            override fun onPreExecute() {
                super.onPreExecute()
                binding.contentScrollView.visibility = View.GONE
                binding.progressView.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg unit: Unit?) {
                try {
                    progressLatch.await()
                } catch (e: InterruptedException) {
                    MyApplication.Instance.showError(e)
                }
            }

            override fun onPostExecute(result: Unit?) {
                binding.progressView.visibility = View.GONE
                binding.contentScrollView.visibility = View.VISIBLE
            }
        }

        hideProgressTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                progressLatch.countDown()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                progressLatch.countDown()
            }
        }
        binding.webView.loadUrl(story.url)
        MyApplication.Instance.storyService.getComments(story)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ comments ->
                progressLatch.countDown()
                commentAdapter.comments = comments.map { CommentViewModel(it) }
                commentAdapter.notifyDataSetChanged()
            }, { e ->
                progressLatch.countDown()
                MyApplication.Instance.showError(e)
            })
            .disposeOnLifecycle(this, ON_DESTROY)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                val intent = Intent().apply {
                    putExtra(READ_ARTICLE_ID, this@StoryDetailActivity.story.id)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            R.id.refresh -> {
                loadUrlAndComments()
                return true
            }
            R.id.exit -> {
                this.finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_COMMENTS, commentsJsonAdapter.toJson(commentAdapter.comments.map { it.comment }))

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressTask?.run {
            if (!isCancelled) cancel(true)
        }
    }
}
