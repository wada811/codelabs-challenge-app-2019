package droidkaigi.github.io.challenge2019.infra.db

import android.content.Context
import android.preference.PreferenceManager

class ArticlePreferences(
    private val context: Context
) {

    companion object {
        private const val ARTICLE_IDS_KEY = "article_ids_key"
    }

    fun saveArticleIds(articleId: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val articleIds = mutableSetOf<String>().also {
            it.addAll(preferences.getStringSet(ARTICLE_IDS_KEY, mutableSetOf()) ?: mutableSetOf())
            it.add(articleId)
        }
        preferences.edit().putStringSet(ARTICLE_IDS_KEY, articleIds).apply()
    }

    fun getArticleIds(): Set<String> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getStringSet(ARTICLE_IDS_KEY, setOf()) ?: setOf()
    }
}
