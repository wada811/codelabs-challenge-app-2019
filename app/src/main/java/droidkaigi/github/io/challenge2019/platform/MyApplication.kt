package droidkaigi.github.io.challenge2019.platform

import android.app.Application
import android.os.Build
import android.widget.Toast
import com.facebook.stetho.Stetho
import com.squareup.moshi.Moshi
import droidkaigi.github.io.challenge2019.BuildConfig
import droidkaigi.github.io.challenge2019.infra.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.infra.db.ArticlePreferences
import droidkaigi.github.io.challenge2019.infra.repository.StoryRepository
import droidkaigi.github.io.challenge2019.service.StoryService
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import timber.log.Timber.DebugTree


class MyApplication : Application() {
    companion object {
        private const val Tag = "Codelabs"
        fun tag(tag: String) = "$Tag:$tag"

        lateinit var Instance: MyApplication
    }

    val storyService: StoryService = StoryService(StoryRepository(HackerNewsApi.apiClient(), ArticlePreferences(this)))
    val moshi: Moshi = Moshi.Builder().build()

    override fun onCreate() {
        super.onCreate()
        Instance = this

        JodaTimeAndroid.init(this)

        if (BuildConfig.DEBUG && !isRoboUnitTest()) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }

    private fun isRoboUnitTest(): Boolean {
        return "robolectric" == Build.FINGERPRINT
    }

    fun showError(throwable: Throwable) {
        Timber.tag("error").v(throwable)
        Toast.makeText(applicationContext, throwable.message, Toast.LENGTH_SHORT).show()
    }
}
