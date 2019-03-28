package droidkaigi.github.io.challenge2019.infra.repository

import droidkaigi.github.io.challenge2019.domain.Comment
import droidkaigi.github.io.challenge2019.domain.Story
import droidkaigi.github.io.challenge2019.infra.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.infra.db.ArticlePreferences
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class StoryRepository(
    private val api: HackerNewsApi,
    private val db: ArticlePreferences
) {

    fun getTopStories(): Single<List<Story>> {
        return api.getTopStories()
            .toObservable()
            .flatMapIterable { it }
            .take(20)
            .flatMapSingle({ getStory(it) }, true)
            .toList()
    }

    fun getStory(id: Long): Single<Story> {
        return api.getItem(id)
            .map { response ->
                Story(
                    response.id,
                    response.author,
                    ZonedDateTime.ofInstant(Instant.ofEpochSecond(response.time), ZoneId.systemDefault()),
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

    fun getComments(story: Story): Single<List<Comment>> {
        return Observable.fromIterable(story.kids)
            .flatMapSingle({ api.getItem(it) }, true)
            .map { Comment(it.author, it.text) }
            .toList()
    }
}
