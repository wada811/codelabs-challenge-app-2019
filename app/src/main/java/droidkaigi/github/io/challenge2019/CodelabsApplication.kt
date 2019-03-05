package droidkaigi.github.io.challenge2019

import android.app.Application
import android.os.Build
import com.facebook.stetho.Stetho
import droidkaigi.github.io.challenge2019.infra.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.infra.db.ArticlePreferences
import droidkaigi.github.io.challenge2019.infra.repository.StoryRepository
import droidkaigi.github.io.challenge2019.service.ApplicationService
import droidkaigi.github.io.challenge2019.service.ApplicationServiceProvider
import droidkaigi.github.io.challenge2019.service.StoryService
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import timber.log.Timber.DebugTree

class CodelabsApplication : Application(), ApplicationServiceProvider {
    override val instance: ApplicationService by lazy {
        ApplicationService(
            applicationContext,
            StoryService(StoryRepository(HackerNewsApi.apiClient(), ArticlePreferences(this)))
        )
    }

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        if (BuildConfig.DEBUG && !isRoboUnitTest()) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }

    private fun isRoboUnitTest(): Boolean {
        return "robolectric" == Build.FINGERPRINT
    }

}
