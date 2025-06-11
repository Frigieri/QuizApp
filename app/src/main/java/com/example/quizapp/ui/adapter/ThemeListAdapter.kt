package com.example.quizapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.data.model.Theme
import com.example.quizapp.databinding.ItemThemeBinding


class ThemeListAdapter(
    private val onItemClicked: (Theme) -> Unit
) : ListAdapter<Theme, ThemeListAdapter.ThemeViewHolder>(ThemeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val binding = ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val theme = getItem(position)
        holder.bind(theme)
        holder.itemView.setOnClickListener {
            onItemClicked(theme)
        }
    }

    inner class ThemeViewHolder(private val binding: ItemThemeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(theme: Theme) {
            binding.textViewThemeName.text = theme.name
            binding.textViewThemeDescription.text = theme.description

        }
    }
}


class ThemeDiffCallback : DiffUtil.ItemCallback<Theme>() {
    override fun areItemsTheSame(oldItem: Theme, newItem: Theme): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Theme, newItem: Theme): Boolean {
        return oldItem == newItem
    }
}