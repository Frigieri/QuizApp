package com.example.quizapp.ui.custom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.data.model.Question
import com.example.quizapp.databinding.FragmentCustomQuestionBinding
import com.example.quizapp.ui.adapter.CustomQuestionAdapter
import com.example.quizapp.util.DataResult
import com.example.quizapp.ui.ViewModels.CustomQuestionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomQuestionFragment : Fragment() {

    private var _binding: FragmentCustomQuestionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CustomQuestionViewModel by viewModels()
    private val args: CustomQuestionFragmentArgs by navArgs()
    private lateinit var adapter: CustomQuestionAdapter

    private var currentQuestionId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeId = args.customThemeId
        val isCustomTheme = args.isCustomTheme

        Log.d("CustomQuestionFragment", "onViewCreated: Received themeId: $themeId, isCustomTheme: $isCustomTheme")

        viewModel.setThemeId(themeId)
        viewModel.loadQuestions(isCustomTheme)
        Log.d("CustomQuestionFragment", "Calling viewModel.loadQuestions($isCustomTheme)")

        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = CustomQuestionAdapter(
            onEditClick = { question ->
                Log.d("CustomQuestionFragment", "Edit clicked for question: ${question.id}")
                if (question.isCustom) {
                    currentQuestionId = question.id
                    // CORREÇÃO AQUI: Acessando question.questionText
                    binding.editTextQuestionText.setText(question.questionText)
                    binding.editTextOption1.setText(question.options[0])
                    binding.editTextOption2.setText(question.options[1])
                    binding.editTextOption3.setText(question.options[2])
                    binding.editTextOption4.setText(question.options[3])
                    binding.editTextCorrectOptionIndex.setText(question.correctOptionIndex.toString())
                    binding.buttonAddUpdateQuestion.text = "Atualizar Pergunta"
                } else {
                    Toast.makeText(context, "Não é possível editar perguntas padrão.", Toast.LENGTH_SHORT).show()
                    Log.d("CustomQuestionFragment", "Attempted to edit non-custom question: ${question.id}")
                }
            },
            onDeleteClick = { question ->
                Log.d("CustomQuestionFragment", "Delete clicked for question: ${question.id}")
                if (question.isCustom) {
                    viewModel.deleteQuestion(question.id)
                } else {
                    Toast.makeText(context, "Não é possível excluir perguntas padrão.", Toast.LENGTH_SHORT).show()
                    Log.d("CustomQuestionFragment", "Attempted to delete non-custom question: ${question.id}")
                }
            }
        )
        binding.recyclerViewCustomQuestions.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCustomQuestions.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.questions.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataResult.Loading -> {
                    Log.d("CustomQuestionFragment", "ViewModel observed: Loading questions for CustomQuestionFragment.")
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewCustomQuestions.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                    binding.layoutAddQuestion.visibility = View.GONE
                }
                is DataResult.Success -> {
                    Log.d("CustomQuestionFragment", "ViewModel observed: Success. Questions loaded: ${it.data.size} for CustomQuestionFragment.")
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewCustomQuestions.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE

                    if (args.isCustomTheme) {
                        binding.layoutAddQuestion.visibility = View.VISIBLE
                        Log.d("CustomQuestionFragment", "Displaying Add/Update form for custom theme.")
                    } else {
                        binding.layoutAddQuestion.visibility = View.GONE
                        binding.textViewError.text = "Este é um tema padrão. Perguntas não podem ser adicionadas ou modificadas por aqui."
                        binding.textViewError.visibility = View.VISIBLE
                        Log.d("CustomQuestionFragment", "Hiding Add/Update form for standard theme.")
                    }

                    adapter.submitList(it.data)
                }
                is DataResult.Error -> {
                    Log.e("CustomQuestionFragment", "ViewModel observed: Error loading questions for CustomQuestionFragment: ${it.exception.message}", it.exception)
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewCustomQuestions.visibility = View.GONE
                    binding.textViewError.visibility = View.VISIBLE
                    binding.textViewError.text = "Erro ao carregar perguntas: ${it.exception.message}"
                    Toast.makeText(context, "Erro: ${it.exception.message}", Toast.LENGTH_LONG).show()
                    binding.layoutAddQuestion.visibility = View.GONE
                }
            }
        })

        viewModel.addUpdateQuestionResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataResult.Success -> {
                    val message = if (currentQuestionId == null) "Pergunta adicionada com sucesso!" else "Pergunta atualizada com sucesso!"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    clearForm()
                    viewModel.loadQuestions(args.isCustomTheme)
                    Log.d("CustomQuestionFragment", "Add/Update successful. Reloading questions for theme: ${args.customThemeId}, isCustom: ${args.isCustomTheme}")
                }
                is DataResult.Error -> {
                    Toast.makeText(context, "Erro: ${it.exception.message}", Toast.LENGTH_LONG).show()
                    Log.e("CustomQuestionFragment", "Add/Update failed: ${it.exception.message}", it.exception)
                }
                is DataResult.Loading -> {}
            }
        })

        viewModel.deleteQuestionResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataResult.Success -> {
                    Toast.makeText(context, "Pergunta excluída com sucesso!", Toast.LENGTH_SHORT).show()
                    viewModel.loadQuestions(args.isCustomTheme)
                    Log.d("CustomQuestionFragment", "Delete successful. Reloading questions for theme: ${args.customThemeId}, isCustom: ${args.isCustomTheme}")
                }
                is DataResult.Error -> {
                    Toast.makeText(context, "Erro ao excluir pergunta: ${it.exception.message}", Toast.LENGTH_LONG).show()
                    Log.e("CustomQuestionFragment", "Delete failed: ${it.exception.message}", it.exception)
                }
                is DataResult.Loading -> { /* Handle loading state if needed */ }
            }
        })
    }

    private fun setupListeners() {
        binding.buttonAddUpdateQuestion.setOnClickListener {
            val questionText = binding.editTextQuestionText.text.toString().trim()
            val option1 = binding.editTextOption1.text.toString().trim()
            val option2 = binding.editTextOption2.text.toString().trim()
            val option3 = binding.editTextOption3.text.toString().trim()
            val option4 = binding.editTextOption4.text.toString().trim()
            val correctOptionIndex = binding.editTextCorrectOptionIndex.text.toString().trim().toIntOrNull()

            if (correctOptionIndex != null && (correctOptionIndex < 0 || correctOptionIndex > 3)) {
                Toast.makeText(context, "O índice da opção correta deve ser entre 0 e 3.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (questionText.isNotEmpty() && option1.isNotEmpty() && option2.isNotEmpty() &&
                option3.isNotEmpty() && option4.isNotEmpty() && correctOptionIndex != null) {

                if (args.isCustomTheme) {
                    val options = listOf(option1, option2, option3, option4)
                    val question = Question(
                        id = currentQuestionId ?: "",
                        questionText = questionText,
                        options = options,
                        correctOptionIndex = correctOptionIndex,
                        themeId = args.customThemeId,
                        isCustom = true
                    )
                    Log.d("CustomQuestionFragment", "Attempting to add/update question. Question ID: ${question.id}, Theme ID: ${question.themeId}")

                    if (currentQuestionId == null) {
                        viewModel.addQuestion(question)
                    } else {
                        viewModel.updateQuestion(question)
                    }
                } else {
                    Toast.makeText(context, "Não é possível adicionar ou editar perguntas para temas padrão.", Toast.LENGTH_SHORT).show()
                    clearForm()
                    Log.d("CustomQuestionFragment", "Attempted to add/update question for standard theme. Operation denied.")
                }
            } else {
                Toast.makeText(context, "Por favor, preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                Log.d("CustomQuestionFragment", "Form validation failed: Not all fields filled.")
            }
        }
    }

    private fun clearForm() {
        binding.editTextQuestionText.text?.clear()
        binding.editTextOption1.text?.clear()
        binding.editTextOption2.text?.clear()
        binding.editTextOption3.text?.clear()
        binding.editTextOption4.text?.clear()
        binding.editTextCorrectOptionIndex.text?.clear()
        binding.buttonAddUpdateQuestion.text = "Adicionar Pergunta"
        currentQuestionId = null
        Log.d("CustomQuestionFragment", "Form cleared.")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("CustomQuestionFragment", "onDestroyView: Fragment destroyed.")
    }
}