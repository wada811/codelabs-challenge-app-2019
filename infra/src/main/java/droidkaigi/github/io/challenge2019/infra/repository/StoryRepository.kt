package droidkaigi.github.io.challenge2019.infra.repository

import droidkaigi.github.io.challenge2019.domain.Comment
import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.infra.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.infra.db.ArticlePreferences
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class StoryRepository(
    private val api: HackerNewsApi,
    private val db: ArticlePreferences
) {

    fun getTopStories(): Single<List<droidkaigi.github.io.challenge2019.domain.Story>> {
        return api.getTopStories()
            .toObservable()
            .take(20)
            .flatMapIterable { it }
            .flatMapSingle({ getStory(it) }, true)
            .toList()
    }

    fun getStory(id: Long): Single<droidkaigi.github.io.challenge2019.domain.Story> {
        return api.getItem(id)
            .map { response ->
                droidkaigi.github.io.challenge2019.domain.Story(
                    response.id,
                    response.author,
                    response.time,
                    response.text,
                    response.kids,
                    response.url,
                    response.score,
                    response.title,
                    db.getArticleIds().any { it == response.id.toString() }
                )
            }
    }

    fun saveReadStatus(id: Long): Completable {
        return Completable.fromAction {
            db.saveArticleIds(id.toString())
        }
    }

    fun getComments(story: droidkaigi.github.io.challenge2019.domain.Story): Single<List<droidkaigi.github.io.challenge2019.domain.Comment>> {
        return Observable.fromIterable(story.kids)
            .flatMapSingle({ api.getItem(it) }, true)
            .map { droidkaigi.github.io.challenge2019.domain.Comment(it.author, it.text) }
            .toList()
    }
}
