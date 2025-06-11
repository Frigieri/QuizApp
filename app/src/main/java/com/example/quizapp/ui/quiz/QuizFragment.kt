package com.example.quizapp.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizapp.R
import com.example.quizapp.data.model.Question
import com.example.quizapp.databinding.FragmentQuizBinding
import com.example.quizapp.util.DataResult
import com.example.quizapp.viewmodel.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()
    private val args: QuizFragmentArgs by navArgs()

    private var currentQuestionIndex = 0
    private var score = 0
    private var questions = listOf<Question>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeId = args.themeId
        val isCustomTheme = args.isCustomTheme
        Log.d("QuizFragment", "onViewCreated: Fragment started. Received themeId: $themeId, isCustomTheme: $isCustomTheme")

        setupAnswerButtons()

        viewModel.questions.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DataResult.Loading -> {
                    Log.d("QuizFragment", "ViewModel observed: Loading questions.")
                    showLoading(true)
                }
                is DataResult.Success -> {
                    showLoading(false)
                    questions = result.data
                    Log.d("QuizFragment", "ViewModel observed: Success. Questions loaded: ${questions.size} for themeId: $themeId, isCustomTheme: $isCustomTheme")
                    if (questions.isNotEmpty()) {
                        displayQuestion(currentQuestionIndex)
                    } else {
                        val message = "Nenhuma pergunta encontrada para este tema: $themeId"
                        Log.d("QuizFragment", "ViewModel observed: Success, but question list is empty. Message: $message")
                        showError(message)
                    }
                }
                is DataResult.Error -> {
                    showLoading(false)
                    val message = "Erro ao carregar perguntas para tema $themeId: ${result.exception.message}"
                    Log.e("QuizFragment", "ViewModel observed: Error loading questions. Message: $message", result.exception)
                    showError(message)
                }
            }
        }

        Log.d("QuizFragment", "Calling viewModel.loadQuestions($themeId, $isCustomTheme)")
        viewModel.loadQuestions(themeId, isCustomTheme)
    }

    private fun setupAnswerButtons() {
        binding.buttonOptionA.setOnClickListener { checkAnswer(0) }
        binding.buttonOptionB.setOnClickListener { checkAnswer(1) }
        binding.buttonOptionC.setOnClickListener { checkAnswer(2) }
        binding.buttonOptionD.setOnClickListener { checkAnswer(3) }
        binding.buttonNext.setOnClickListener { moveToNextQuestion() }
    }

    private fun displayQuestion(index: Int) {
        if (index < 0 || index >= questions.size) return

        val question = questions[index]
        // CORREÇÃO AQUI: De question.text para question.questionText
        binding.textViewQuestion.text = question.questionText
        binding.buttonOptionA.text = question.options[0]
        binding.buttonOptionB.text = question.options[1]
        binding.buttonOptionC.text = question.options[2]
        binding.buttonOptionD.text = question.options[3]

        binding.textViewQuestionCounter.text = "Pergunta ${index + 1} de ${questions.size}"

        resetButtonStates()
        binding.buttonNext.visibility = View.INVISIBLE
        Log.d("QuizFragment", "Displaying question ${index + 1}: ${question.questionText}")
    }

    private fun checkAnswer(selectedOptionIndex: Int) {
        if (currentQuestionIndex >= questions.size) return

        val question = questions[currentQuestionIndex]
        val isCorrect = selectedOptionIndex == question.correctOptionIndex

        disableAnswerButtons()

        highlightAnswer(selectedOptionIndex, isCorrect)

        if (isCorrect) {
            score++
            Log.d("QuizFragment", "Correct answer! Current score: $score")
        } else {
            Log.d("QuizFragment", "Incorrect answer.")
        }

        binding.buttonNext.visibility = View.VISIBLE
    }

    private fun moveToNextQuestion() {
        Log.d("QuizFragment", "Moving to next question. Current index: $currentQuestionIndex")
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            displayQuestion(currentQuestionIndex)
        } else {
            Log.d("QuizFragment", "End of quiz. Final score: $score/${questions.size}")

            val action = QuizFragmentDirections.actionQuizFragmentToResultFragment(
                score = score,
                totalQuestions = questions.size,
                themeId = args.themeId,
                isCustomTheme = args.isCustomTheme
            )
            findNavController().navigate(action)
        }
    }

    private fun resetButtonStates() {
        binding.buttonOptionA.isEnabled = true
        binding.buttonOptionB.isEnabled = true
        binding.buttonOptionC.isEnabled = true
        binding.buttonOptionD.isEnabled = true

        binding.buttonOptionA.setBackgroundResource(R.drawable.button_normal)
        binding.buttonOptionB.setBackgroundResource(R.drawable.button_normal)
        binding.buttonOptionC.setBackgroundResource(R.drawable.button_normal)
        binding.buttonOptionD.setBackgroundResource(R.drawable.button_normal)
    }

    private fun disableAnswerButtons() {
        binding.buttonOptionA.isEnabled = false
        binding.buttonOptionB.isEnabled = false
        binding.buttonOptionC.isEnabled = false
        binding.buttonOptionD.isEnabled = false
    }

    private fun highlightAnswer(selectedIndex: Int, isCorrect: Boolean) {
        val correctIndex = questions[currentQuestionIndex].correctOptionIndex

        val selectedButton = when (selectedIndex) {
            0 -> binding.buttonOptionA
            1 -> binding.buttonOptionB
            2 -> binding.buttonOptionC
            else -> binding.buttonOptionD
        }

        if (isCorrect) {
            selectedButton.setBackgroundResource(R.drawable.button_correct)
        } else {
            selectedButton.setBackgroundResource(R.drawable.button_incorrect)
            val correctButton = when (correctIndex) {
                0 -> binding.buttonOptionA
                1 -> binding.buttonOptionB
                2 -> binding.buttonOptionC
                else -> binding.buttonOptionD
            }
            correctButton.setBackgroundResource(R.drawable.button_correct)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.groupQuizContent.visibility = if (isLoading) View.GONE else View.VISIBLE
        Log.d("QuizFragment", "Loading visibility set to: $isLoading")
    }

    private fun showError(message: String) {
        binding.textViewError.text = message
        binding.textViewError.visibility = View.VISIBLE
        binding.groupQuizContent.visibility = View.GONE
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.e("QuizFragment", "Displaying error message: $message")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("QuizFragment", "onDestroyView: Fragment destroyed.")
    }
}