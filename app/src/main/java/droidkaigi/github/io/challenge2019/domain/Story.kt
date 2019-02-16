package droidkaigi.github.io.challenge2019.domain

data class Story(
    val id: Long,
    val author: String,
    val time: Long = 0L,
    val text: String? = "",
    val kids: List<Long> = emptyList(),
    val url: String = "",
    val score: Int = 0,
    val title: String = ""
)
