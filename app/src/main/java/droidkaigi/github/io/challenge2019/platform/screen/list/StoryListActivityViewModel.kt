package droidkaigi.github.io.challenge2019.platform.screen.list

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.domain.LoadState.Loading
import droidkaigi.github.io.challenge2019.service.StoryService

class StoryListActivityViewModel(
    private val storyService: StoryService
) : ViewModel() {

    val isLoading: ObservableField<Boolean> =
        ObservableField(storyService.loadState.value == Loading).also { field ->
            storyService.loadState.subscribe { field.set(it == Loading) }
        }
    val stories: ObservableArrayList<StoryListItemViewModel> =
        ObservableArrayList<StoryListItemViewModel>().also {
            it.clear()
            it.addAll(storyService.stories.value!!.map(::StoryListItemViewModel))
        }

    fun loadTopStories() = storyService.getTopStories()
    fun loadStory(id: Long) = storyService.getStory(id)
}
