package droidkaigi.github.io.challenge2019

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val ACTIVITY_REQUEST = 1
    }

    internal val moshi = Moshi.Builder().build()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.exit -> {
                this.finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun startActivityForResult(intent: Intent?) {
        intent?.let { intent2 ->
            startActivityForResult(intent2, ACTIVITY_REQUEST)
        }
    }
}
