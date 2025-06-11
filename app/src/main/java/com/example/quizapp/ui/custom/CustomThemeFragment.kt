package com.example.quizapp.ui.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.databinding.FragmentCustomThemeBinding
import com.example.quizapp.ui.adapter.CustomThemeAdapter
import com.example.quizapp.util.DataResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomThemeFragment : Fragment() {

    private var _binding: FragmentCustomThemeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CustomThemeViewModel by viewModels()
    private lateinit var adapter: CustomThemeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = CustomThemeAdapter(
            onThemeClicked = { theme -> },
            onEditClicked = { theme ->

                val action = CustomThemeFragmentDirections.actionCustomThemeFragmentToCustomQuestionFragment(
                    customThemeId = theme.id,
                    isCustomTheme = theme.isCustom
                )
                findNavController().navigate(action)
            },
            onDeleteClicked = { theme ->
                viewModel.deleteTheme(theme.id)
            }
        )
        binding.recyclerViewCustomThemes.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCustomThemes.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.customThemes.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataResult.Loading -> {
                    binding.progressBarCustomThemes.visibility = View.VISIBLE
                    binding.recyclerViewCustomThemes.visibility = View.GONE
                    binding.textViewErrorCustomThemes.visibility = View.GONE
                }
                is DataResult.Success -> {
                    binding.progressBarCustomThemes.visibility = View.GONE
                    binding.recyclerViewCustomThemes.visibility = View.VISIBLE
                    binding.textViewErrorCustomThemes.visibility = View.GONE
                    adapter.submitList(it.data)
                }
                is DataResult.Error -> {
                    binding.progressBarCustomThemes.visibility = View.GONE
                    binding.recyclerViewCustomThemes.visibility = View.GONE
                    binding.textViewErrorCustomThemes.visibility = View.VISIBLE
                    binding.textViewErrorCustomThemes.text = "Erro ao carregar temas: ${it.exception.message}"
                    Toast.makeText(context, "Erro: ${it.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.addThemeResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataResult.Success -> {
                    Toast.makeText(context, "Tema adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    binding.editTextThemeName.text?.clear()
                    binding.editTextThemeDescription.text?.clear()
                    viewModel.loadCustomThemes()
                }
                is DataResult.Error -> {
                    Toast.makeText(context, "Erro ao adicionar tema: ${it.exception.message}", Toast.LENGTH_LONG).show()
                }
                is DataResult.Loading -> {  }
            }
        })

        viewModel.deleteThemeResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataResult.Success -> {
                    Toast.makeText(context, "Tema excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    viewModel.loadCustomThemes()
                }
                is DataResult.Error -> {
                    Toast.makeText(context, "Erro ao excluir tema: ${it.exception.message}", Toast.LENGTH_LONG).show()
                }
                is DataResult.Loading -> { }
            }
        })
    }

    private fun setupListeners() {
        binding.buttonAddTheme.setOnClickListener {
            val name = binding.editTextThemeName.text.toString().trim()
            val description = binding.editTextThemeDescription.text.toString().trim()
            if (name.isNotEmpty() && description.isNotEmpty()) {
                viewModel.addTheme(name, description)
            } else {
                Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}