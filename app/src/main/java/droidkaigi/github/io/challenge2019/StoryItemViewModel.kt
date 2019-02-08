package droidkaigi.github.io.challenge2019

import androidx.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.data.api.response.Item
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit

class StoryItemViewModel(val item: Item) : ViewModel() {
    val id = item.id
    val title = item.title
    val time = DateTimeFormat.forPattern("yyyy/M/d H:MM").print(TimeUnit.SECONDS.toMillis(item.time))!!
    val url = item.url
    val scoreAndAuthor = "${item.score} points by ${item.author}"
}