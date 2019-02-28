package droidkaigi.github.io.challenge2019.platform.screen.list

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.wada811.lifecycledisposable.disposeOnLifecycle
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.databinding.StoryListActivityBinding
import droidkaigi.github.io.challenge2019.ingest.IngestManager
import droidkaigi.github.io.challenge2019.platform.MyApplication
import droidkaigi.github.io.challenge2019.platform.screen.detail.StoryDetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class StoryListActivity : AppCompatActivity() {

    private fun <T : ViewDataBinding> ComponentActivity.bind(layoutId: Int): T = DataBindingUtil.setContentView(this, layoutId)
    private val binding by lazy { bind<StoryListActivityBinding>(R.layout.story_list_activity) }
    private val viewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = StoryListActivityViewModel(MyApplication.Instance.storyService) as T
        }).get(StoryListActivityViewModel::class.java)
    }
    private lateinit var storyListItemAdapter: StoryListItemAdapter
    private val ingestManager = IngestManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        binding.itemRecyclerView.addItemDecoration(DividerItemDecoration(binding.itemRecyclerView.context, DividerItemDecoration.VERTICAL))
        storyListItemAdapter = StoryListItemAdapter(
            stories = viewModel.stories,
            onClickItem = { itemViewModel ->
                MyApplication.Instance.storyService.currentStory = itemViewModel.story
                startActivity(Intent(this@StoryListActivity, StoryDetailActivity::class.java))
            },
            onClickMenuItem = { itemViewModel, menuItemId ->
                when (menuItemId) {
                    R.id.copy_url -> {
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.primaryClip = ClipData.newPlainText("url", itemViewModel.url)
                        trackAction()
                    }
                    R.id.refresh -> {
                        viewModel.loadStory(itemViewModel.id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ item ->
                                val index = storyListItemAdapter.stories.indexOf(itemViewModel)
                                if (index == -1) return@subscribe

                                storyListItemAdapter.stories[index] = StoryListItemViewModel(item)
                                runOnUiThread {
                                    storyListItemAdapter.notifyItemChanged(index)
                                }
                            }, { e ->
                                MyApplication.Instance.showError(e)
                            })
                            .disposeOnLifecycle(this, ON_DESTROY)
                    }
                }
            }
        )
        binding.itemRecyclerView.adapter = storyListItemAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.isSwipeRefreshing.set(true)
            loadTopStories()
        }

        viewModel.isShowProgressView.set(true)
        loadTopStories()
    }

    private fun loadTopStories() {
        viewModel.loadTopStories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stories ->
                storyListItemAdapter.stories = stories.map { story -> StoryListItemViewModel(story) }.toMutableList()
                storyListItemAdapter.notifyDataSetChanged()
                trackPageView()
            }, { e ->
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
            R.id.refresh -> {
                viewModel.isShowProgressView.set(true)
                loadTopStories()
                return true
            }
            R.id.exit -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun trackPageView() {
        Timber.tag(MyApplication.tag("Tracking")).d("trackPageView")
        Thread {
            while (ingestManager.track() != 200) {
            }
        }.start()
    }

    private fun trackAction() {
        Timber.tag(MyApplication.tag("Tracking")).d("trackAction")
        Thread {
            while (ingestManager.track() != 200) {
            }
        }.start()
    }
}
