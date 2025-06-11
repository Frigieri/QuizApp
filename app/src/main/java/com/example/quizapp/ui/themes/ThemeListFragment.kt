package com.example.quizapp.ui.themes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.data.model.Theme
import com.example.quizapp.databinding.FragmentThemeListBinding
import com.example.quizapp.ui.adapter.ThemeListAdapter
import com.example.quizapp.util.DataResult
import com.example.quizapp.viewmodel.ThemeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeListFragment : Fragment() {

    private var _binding: FragmentThemeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ThemeListViewModel by viewModels()
    private lateinit var themeListAdapter: ThemeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ThemeListFragment", "onViewCreated: Fragment started.")

        setupRecyclerView()
        setupListeners()
        observeViewModel()


        viewModel.loadThemes()
    }

    private fun setupRecyclerView() {
        // Ação de clique do item do tema
        themeListAdapter = ThemeListAdapter { theme ->
            Log.d("ThemeListFragment", "Theme clicked: ID=${theme.id}, Name=${theme.name}, isCustom=${theme.isCustom}")
            try {
                // Navega para QuizFragment, passando themeId E isCustom
                val action = ThemeListFragmentDirections.actionThemeListFragmentToQuizFragment(
                    themeId = theme.id,
                    isCustomTheme = theme.isCustom
                )
                findNavController().navigate(action)
                Log.d("ThemeListFragment", "Navigating to QuizFragment with themeId: ${theme.id}, isCustom: ${theme.isCustom}")
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "Erro de navegação: Ação não encontrada.", Toast.LENGTH_SHORT).show()
                Log.e("ThemeListFragment", "Navigation error to QuizFragment: ${e.message}", e)
            }
        }
        binding.recyclerViewThemes.adapter = themeListAdapter
    }

    private fun setupListeners() {
        binding.buttonCustomThemes.setOnClickListener {
            Log.d("ThemeListFragment", "Custom Themes button clicked. Navigating to CustomThemeFragment.")
            findNavController().navigate(R.id.action_themeListFragment_to_customThemeFragment)
        }
        binding.buttonProfile.setOnClickListener {
            findNavController().navigate(R.id.action_themeListFragment_to_profileFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.themes.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DataResult.Loading -> {
                    Log.d("ThemeListFragment", "ViewModel observed: Loading themes.")
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewThemes.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                }
                is DataResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.data.isNotEmpty()) {
                        themeListAdapter.submitList(result.data)
                        binding.recyclerViewThemes.visibility = View.VISIBLE
                        binding.textViewError.visibility = View.GONE
                        Log.d("ThemeListFragment", "ViewModel observed: Success. Themes loaded: ${result.data.size}")
                    } else {
                        binding.recyclerViewThemes.visibility = View.GONE
                        binding.textViewError.visibility = View.VISIBLE
                        binding.textViewError.text = "Nenhum tema encontrado."
                        Log.d("ThemeListFragment", "ViewModel observed: Success. No themes found.")
                    }
                }
                is DataResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewThemes.visibility = View.GONE
                    binding.textViewError.visibility = View.VISIBLE
                    binding.textViewError.text = "Erro ao carregar temas: ${result.exception.message}"
                    Log.e("ThemeListFragment", "ViewModel observed: Error loading themes: ${result.exception.message}", result.exception)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}