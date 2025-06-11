package com.example.quizapp.ui.custom

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.data.model.Theme
import com.example.quizapp.databinding.ItemThemeBinding
import com.example.quizapp.util.ThemeDiffCallback

class CustomThemeListAdapter(
    private val onThemeClicked: (Theme) -> Unit,
    private val onEditClicked: (Theme) -> Unit,
    private val onDeleteClicked: (Theme) -> Unit
) : ListAdapter<Theme, CustomThemeListAdapter.CustomThemeViewHolder>(ThemeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomThemeViewHolder {
        val binding = ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomThemeViewHolder, position: Int) {
        val theme = getItem(position)
        holder.bind(theme)
    }

    inner class CustomThemeViewHolder(private val binding: ItemThemeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(theme: Theme) {
            binding.textViewThemeName.text = theme.name
            binding.textViewThemeDescription.text = theme.description



            itemView.setOnClickListener {
                onThemeClicked(theme)
            }


            itemView.setOnLongClickListener { view ->
                showPopupMenu(view, theme)
                true
            }

        }

        private fun showPopupMenu(view: View, theme: Theme) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.custom_item_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEditClicked(theme)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClicked(theme)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}



