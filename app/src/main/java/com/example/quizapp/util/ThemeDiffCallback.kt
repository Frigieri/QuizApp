package com.example.quizapp.util

import androidx.recyclerview.widget.DiffUtil
import com.example.quizapp.data.model.Theme

class ThemeDiffCallback : DiffUtil.ItemCallback<Theme>() {
    override fun areItemsTheSame(oldItem: Theme, newItem: Theme): Boolean {

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Theme, newItem: Theme): Boolean {

        return oldItem == newItem
    }
}
