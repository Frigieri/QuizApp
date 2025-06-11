
package com.example.quizapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.data.model.Theme
import com.example.quizapp.databinding.ItemCustomThemeBinding //
import com.example.quizapp.util.ThemeDiffCallback
import android.util.Log // Import para logs pra achar a desgraça do erro

class CustomThemeAdapter(
    private val onThemeClicked: (Theme) -> Unit, //
    private val onEditClicked: (Theme) -> Unit, //
    private val onDeleteClicked: (Theme) -> Unit //
) : ListAdapter<Theme, CustomThemeAdapter.CustomThemeViewHolder>(ThemeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomThemeViewHolder {

        val binding = ItemCustomThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomThemeViewHolder, position: Int) {
        val theme = getItem(position)
        Log.d("CustomThemeAdapter", "Binding theme: ${theme.name} (ID: ${theme.id}) at position $position")
        holder.bind(theme)
    }

    inner class CustomThemeViewHolder(private val binding: ItemCustomThemeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(theme: Theme) {
            // Vincular os TextViews aos IDs corretos do seu item_custom_theme.xml NÃO FUNCIONA
            binding.textViewCustomThemeName.text = theme.name
            binding.textViewCustomThemeDescription.text = theme.description


            itemView.setOnClickListener {
                onThemeClicked(theme)
            }

            // Listeners para os botões "EDITAR" e "EXCLUIR" visíveis
            binding.buttonEditTheme.setOnClickListener {
                Log.d("CustomThemeAdapter", "Botão 'Editar' clicado para o tema ID: ${theme.id}")
                onEditClicked(theme)
            }

            binding.buttonDeleteTheme.setOnClickListener {
                Log.d("CustomThemeAdapter", "Botão 'Excluir' clicado para o tema ID: ${theme.id}")
                onDeleteClicked(theme)
            }


        }


    }
}