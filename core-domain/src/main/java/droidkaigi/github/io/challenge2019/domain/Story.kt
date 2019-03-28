package droidkaigi.github.io.challenge2019.domain

import org.threeten.bp.ZonedDateTime

class Story(
    val id: Long,
    val author: String,
    val time: ZonedDateTime,
    val text: String?,
    val kids: List<Long>,
    val url: String,
    val score: Int,
    val title: String,
    _isRead: Boolean
) {

    var isRead: Boolean = _isRead
        private set

    fun read() {
        isRead = !isRead
    }
}