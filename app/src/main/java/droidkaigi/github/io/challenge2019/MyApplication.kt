package droidkaigi.github.io.challenge2019

import android.app.Application
import android.os.Build
import com.facebook.stetho.Stetho
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import timber.log.Timber.DebugTree


class MyApplication : Application() {
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