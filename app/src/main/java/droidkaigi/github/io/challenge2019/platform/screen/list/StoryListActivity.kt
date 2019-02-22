package droidkaigi.github.io.challenge2019.platform.screen.list

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import droidkaigi.github.io.challenge2019.databinding.StoryListActivityBinding
import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.ingest.IngestManager
import droidkaigi.github.io.challenge2019.platform.MyApplication
import droidkaigi.github.io.challenge2019.platform.screen.detail.StoryDetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

@ContentView(R.layout.story_list_activity)
class StoryListActivity : AppCompatActivity() {

    companion object {
        private const val STATE_STORIES = "stories"
        private const val REQUEST_CODE = 1
    }

    private lateinit var storyListItemAdapter: StoryListItemAdapter

    private val storyJsonAdapter = MyApplication.Instance.moshi.adapter(Story::class.java)
    private val storiesJsonAdapter =
        MyApplication.Instance.moshi.adapter<List<Story>>(Types.newParameterizedType(List::class.java, Story::class.java))

    private val ingestManager = IngestManager()

    private fun <T : ViewDataBinding> ComponentActivity.bind(): T = DataBindingUtil.bind(window.decorView.findViewById(android.R.id.content))!!
    private val binding by lazy { bind<StoryListActivityBinding>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val itemDecoration = DividerItemDecoration(binding.itemRecyclerView.context, DividerItemDecoration.VERTICAL)
        binding.itemRecyclerView.addItemDecoration(itemDecoration)
        storyListItemAdapter = StoryListItemAdapter(
            stories = mutableListOf(),
            onClickItem = { itemViewModel ->
                val itemJson = storyJsonAdapter.toJson(itemViewModel.story)
                val intent = Intent(this@StoryListActivity, StoryDetailActivity::class.java).apply {
                    putExtra(StoryDetailActivity.EXTRA_ITEM_JSON, itemJson)
                }
                startActivityForResult(intent, REQUEST_CODE)
            },
            onClickMenuItem = { itemViewModel, menuItemId ->
                when (menuItemId) {
                    R.id.copy_url -> {
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.primaryClip = ClipData.newPlainText("url", itemViewModel.url)
                        trackAction()
                    }
                    R.id.refresh -> {
                        MyApplication.Instance.storyService.getStory(itemViewModel.id)
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

        binding.swipeRefreshLayout.setOnRefreshListener { loadTopStories() }

        val savedStories = savedInstanceState?.let { bundle ->
            bundle.getString(STATE_STORIES)?.let { storiesJson ->
                storiesJsonAdapter.fromJson(storiesJson)
            }
        }

        if (savedStories != null) {
            storyListItemAdapter.stories = savedStories.map { story -> StoryListItemViewModel(story) }.toMutableList()
            storyListItemAdapter.notifyDataSetChanged()
            return
        }

        loadTopStories()
    }

    private fun loadTopStories() {
        binding.progressView.visibility = View.VISIBLE
        MyApplication.Instance.storyService.getTopStories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stories ->
                binding.progressView.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
                storyListItemAdapter.stories = stories.map { story -> StoryListItemViewModel(story) }.toMutableList()
                storyListItemAdapter.notifyDataSetChanged()
                trackPageView()
            }, { e ->
                MyApplication.Instance.showError(e)
            })
            .disposeOnLifecycle(this, ON_DESTROY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.getLongExtra(StoryDetailActivity.READ_ARTICLE_ID, 0L)?.let { id ->
                    if (id != 0L) {
                        MyApplication.Instance.storyService.saveReadStatus(id)
                        storyListItemAdapter.stories.filter { it.id == id }.forEach { it.read() }
                        storyListItemAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
                loadTopStories()
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
        outState.putString(STATE_STORIES, storiesJsonAdapter.toJson(storyListItemAdapter.stories.map { it.story }))

        super.onSaveInstanceState(outState)
    }

    fun trackPageView() {
        Timber.tag(MyApplication.tag("Tracking")).d("trackPageView")
        Thread {
            while (ingestManager.track() != 200) {
            }
        }.start()
    }

    fun trackAction() {
        Timber.tag(MyApplication.tag("Tracking")).d("trackAction")
        Thread {
            while (ingestManager.track() != 200) {
            }
        }.start()
    }
}
