package droidkaigi.github.io.challenge2019.service

import droidkaigi.github.io.challenge2019.domain.Comment
import droidkaigi.github.io.challenge2019.domain.LoadState
import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.infra.repository.StoryRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class StoryService(
    private val storyRepository: StoryRepository
) {
    var loadState = BehaviorSubject.createDefault(LoadState.None)
    var stories: BehaviorSubject<List<Story>> = BehaviorSubject.createDefault<List<Story>>(listOf())
    fun getTopStories(): Single<List<Story>> {
        return storyRepository.getTopStories()
            .doOnSubscribe { loadState.onNext(LoadState.Loading) }
            .doOnSuccess { loadState.onNext(LoadState.Finished) }
            .doOnError { loadState.onNext(LoadState.Finished) }
            .doOnSuccess { stories.onNext(it) }
    }

    fun getStory(id: Long): Single<Story> {
        return storyRepository.getStory(id)
            .doOnSuccess { story -> stories.onNext(stories.value!!.map { if (it.id == story.id) story else it }) }
    }

    var currentStory: Story? = null
    fun getComments(story: Story): Single<List<Comment>> {
        return storyRepository.getComments(story)
    }

    fun read(story: Story): Completable {
        return storyRepository.saveReadStatus(story.id).doOnSubscribe { story.read() }
    }
}
