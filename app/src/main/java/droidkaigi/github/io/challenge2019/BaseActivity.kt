package droidkaigi.github.io.challenge2019

import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi

abstract class BaseActivity : AppCompatActivity() {

    internal val moshi = Moshi.Builder().build()
}
