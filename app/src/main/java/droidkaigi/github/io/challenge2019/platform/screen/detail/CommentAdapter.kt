package droidkaigi.github.io.challenge2019.platform.screen.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import droidkaigi.github.io.challenge2019.databinding.StoryDetailCommentListItemBinding
import droidkaigi.github.io.challenge2019.platform.screen.detail.CommentAdapter.ViewHolder

class CommentAdapter(
    var comments: List<CommentViewModel>
) : RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(val binding: StoryDetailCommentListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(StoryDetailCommentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.comment = comments[position]
    }
}
