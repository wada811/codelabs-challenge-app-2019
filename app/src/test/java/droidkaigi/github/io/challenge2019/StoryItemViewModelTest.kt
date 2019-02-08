package droidkaigi.github.io.challenge2019

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StoryItemViewModelTest {
    private lateinit var hackerNewsApi: HackerNewsApi
    @Before
    fun setup() {
        hackerNewsApi = HackerNewsApi.apiClient()
    }

    @Test
    fun time() {
        val response = hackerNewsApi.getTopStories().execute()
        response.body()?.let { itemIds ->
            val itemResponse = hackerNewsApi.getItem(itemIds.first()).execute()
            itemResponse.body()?.let { item ->
                val viewModel = StoryItemViewModel(item)
                Truth.assertThat(viewModel.time).isEqualTo("2019/2/8 0:02")
            }
        }
    }
}