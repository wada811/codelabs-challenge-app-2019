package droidkaigi.github.io.challenge2019.story.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep

sealed class StoryActivityAlias : Activity()

@Keep
object StoryListActivityAlias : StoryActivityAlias()

@Keep
object StoryDetailActivityAlias : StoryActivityAlias() {
    fun createIntent(context: Context): Intent = Intent(context, this::class.java)
}
