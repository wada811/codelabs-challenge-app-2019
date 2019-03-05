package droidkaigi.github.io.challenge2019.story.list

import androidx.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.domain.Story
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit

class StoryListItemViewModel(val story: Story) : ViewModel() {
    val id = story.id
    val title = story.title
    val time = DateTimeFormat.forPattern("yyyy/M/d H:MM").print(TimeUnit.SECONDS.toMillis(story.time))!!
    val url = story.url
    val scoreAndAuthor = "${story.score} points by ${story.author}"
    val isRead: Boolean
        get() = story.isRead
}
