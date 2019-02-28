package droidkaigi.github.io.challenge2019.platform.screen.detail

import android.text.Html
import android.view.View
import droidkaigi.github.io.challenge2019.domain.Comment

class CommentViewModel(comment: Comment) {
    val author = comment.author
    val text = if (comment.text != null) Html.fromHtml(comment.text) else null
    val visibility = if (comment.text != null) View.VISIBLE else View.GONE
}
