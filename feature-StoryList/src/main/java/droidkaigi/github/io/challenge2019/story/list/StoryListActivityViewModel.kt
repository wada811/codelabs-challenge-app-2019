package droidkaigi.github.io.challenge2019.story.list

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.domain.LoadState.Finished
import droidkaigi.github.io.challenge2019.domain.LoadState.Loading
import droidkaigi.github.io.challenge2019.service.StoryService

class StoryListActivityViewModel(
    private val storyService: StoryService
) : ViewModel() {

    val isSwipeRefreshing: ObservableField<Boolean> = ObservableField(storyService.loadState.value == Loading).also { field ->
        storyService.loadState
            .filter { field.get()!! }
            .filter { it == Finished }
            .subscribe { field.set(false) }
    }
    val isShowProgressView: ObservableField<Boolean> = ObservableField(storyService.loadState.value == Loading).also { field ->
        storyService.loadState
            .filter { field.get()!! }
            .filter { it == Finished }
            .subscribe { field.set(false) }
    }
    val stories: ObservableArrayList<StoryListItemViewModel> =
        ObservableArrayList<StoryListItemViewModel>().also {
            it.clear()
            it.addAll(storyService.stories.value!!.map(::StoryListItemViewModel))
        }

    fun loadTopStories() = storyService.getTopStories()
    fun loadStory(id: Long) = storyService.getStory(id)
}
