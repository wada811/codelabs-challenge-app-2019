package droidkaigi.github.io.challenge2019.service

import android.app.Activity
import android.content.Context
import android.widget.Toast
import timber.log.Timber

interface ApplicationServiceProvider {
    val instance: ApplicationService
}

class ApplicationService(
    private val applicationContext: Context,
    val storyService: StoryService
) {

    fun showError(throwable: Throwable) {
        Timber.tag(Logger.tag("error")).v(throwable)
        Toast.makeText(applicationContext, throwable.message, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.applicationService(): ApplicationService = (this.application as ApplicationServiceProvider).instance

object Logger {
    private const val Tag = "Codelabs"
    fun tag(tag: String) = "$Tag:$tag"
}
