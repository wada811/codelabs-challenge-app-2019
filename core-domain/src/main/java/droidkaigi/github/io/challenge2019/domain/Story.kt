package droidkaigi.github.io.challenge2019.domain

data class Story(
    val id: Long,
    val author: String,
    val time: Long,
    val text: String?,
    val kids: List<Long>,
    val url: String,
    val score: Int,
    val title: String,
    private val _isRead: Boolean
) {
    var isRead: Boolean = _isRead
        private set

    fun read() {
        isRead = !isRead
    }

    var comments: List<Comment>? = null
}
