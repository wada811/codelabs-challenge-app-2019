package droidkaigi.github.io.challenge2019.platform.screen.list

import androidx.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.service.StoryService

class StoryListViewModel(
    private val storyService: StoryService
) : ViewModel() {
    val stories get() = storyService.stories
    fun loadTopStories() = storyService.getTopStories()
}
