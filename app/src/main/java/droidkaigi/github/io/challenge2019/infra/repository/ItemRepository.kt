package droidkaigi.github.io.challenge2019.infra.repository

import droidkaigi.github.io.challenge2019.domain.Comment
import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.infra.api.HackerNewsApi
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber

class ItemRepository(private val api: HackerNewsApi) {
    fun getStory(id: Long): Single<Story> {
        return api.getItem(id)
            .doOnError { Timber.tag("wada811").e(it, "getStory: $id") }
            .map {
                Story(
                    it.id,
                    it.author,
                    it.time,
                    it.text,
                    it.kids,
                    it.url,
                    it.score,
                    it.title
                )
            }
    }

    fun getTopStories(): Single<List<Story>> {
        return api.getTopStories()
            .toObservable()
            .flatMapIterable { it }
            .take(20)
            .doOnNext { Timber.tag("wada811").d("getTopStories: $it") }
            .flatMapSingle({ getStory(it) }, true)
            .doOnError { Timber.tag("wada811").e(it, "getTopStories: delayErrors") }
            .toList()
    }

    fun getComments(story: Story): Single<List<Comment>> {
        return Observable.fromIterable(story.kids)
            .flatMapSingle({ api.getItem(it) }, true)
            .map { Comment(it.author, it.text) }
            .toList()
    }
}
