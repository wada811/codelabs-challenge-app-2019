package droidkaigi.github.io.challenge2019.platform.screen.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.databinding.StoryListItemBinding
import droidkaigi.github.io.challenge2019.platform.screen.list.StoryListItemAdapter.ViewHolder


class StoryListItemAdapter(
    var stories: MutableList<StoryListItemViewModel>,
    private val onClickItem: ((StoryListItemViewModel) -> Unit)? = null,
    private val onClickMenuItem: ((StoryListItemViewModel, Int) -> Unit)? = null
) : RecyclerView.Adapter<ViewHolder>() {

    class ViewHolder(val binding: StoryListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(StoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewModel = stories[position]

        holder.binding.viewModel = viewModel
        holder.binding.root.setOnClickListener {
            onClickItem?.invoke(viewModel)
        }
        holder.binding.menuButton.setOnClickListener {
            val popupMenu = PopupMenu(holder.binding.menuButton.context, holder.binding.menuButton)
            popupMenu.inflate(R.menu.story_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val menuItemId = menuItem.itemId
                when (menuItemId) {
                    R.id.copy_url,
                    R.id.refresh -> true.also {
                        onClickMenuItem?.invoke(viewModel, menuItemId)
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}
