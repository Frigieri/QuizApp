package com.example.quizapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.data.model.Question
import com.example.quizapp.databinding.ItemCustomQuestionBinding

class CustomQuestionAdapter(
    private val onEditClick: (Question) -> Unit,
    private val onDeleteClick: (Question) -> Unit
) : ListAdapter<Question, CustomQuestionAdapter.CustomQuestionViewHolder>(CustomQuestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomQuestionViewHolder {
        val binding = ItemCustomQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomQuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomQuestionViewHolder, position: Int) {
        val question = getItem(position)
        holder.bind(question)
    }

    inner class CustomQuestionViewHolder(private val binding: ItemCustomQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: Question) {

            binding.textViewQuestionText.text = question.questionText
            binding.textViewOptions.text = question.options.joinToString("\n") { "- $it" }
            binding.textViewCorrectOption.text = "Correta: ${question.correctOptionIndex + 1}"

            binding.buttonEditQuestion.setOnClickListener {
                onEditClick(question)
            }
            binding.buttonDeleteQuestion.setOnClickListener {
                onDeleteClick(question)
            }
        }
    }

    class CustomQuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }
}