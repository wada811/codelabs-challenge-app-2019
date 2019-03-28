package droidkaigi.github.io.challenge2019.story.list

import androidx.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.domain.Story
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class StoryListItemViewModel(val story: Story) : ViewModel() {
    val id: Long = story.id
    val title: String = story.title
    val time: String = story.time.format(DateTimeFormatter.ofPattern("yyyy/M/d H:MM", Locale.getDefault()))
    val url: String = story.url
    val scoreAndAuthor: String = "${story.score} points by ${story.author}"
    val isRead: Boolean = story.isRead
}
