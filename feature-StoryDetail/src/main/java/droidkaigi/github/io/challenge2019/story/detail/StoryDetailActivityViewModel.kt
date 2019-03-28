package droidkaigi.github.io.challenge2019.story.detail

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.domain.LoadState.Loading
import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.service.StoryService
import io.reactivex.Single

class StoryDetailActivityViewModel(
    private val storyService: StoryService,
    private val story: Story
) : ViewModel() {
    val isLoading: ObservableField<Boolean> =
        ObservableField(storyService.loadState.value == Loading).also { field ->
            storyService.loadState.subscribe { field.set(it == Loading) }
        }

    fun read(): Single<Boolean> {
        return storyService.read(story).toSingle { true }
    }

    fun getComments(): Single<List<CommentViewModel>> {
        return storyService.getComments(story)
            .map { it.map(::CommentViewModel) }
    }
}
