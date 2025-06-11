package com.example.quizapp.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat // Importe para ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizapp.R // Certifique-se de que este import está correto para R.color
import com.example.quizapp.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!


    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayResults()
        setupListeners()
    }

    private fun displayResults() {
        val score = args.score
        val totalQuestions = args.totalQuestions
        val themeId = args.themeId
        val isCustomTheme = args.isCustomTheme


        binding.textViewScore.text = "$score / $totalQuestions"

        if (totalQuestions > 0) {
            val percentage = (score.toDouble() / totalQuestions * 100).toInt()
            binding.textViewPercentage.text = "$percentage%"


            val colorResId = when {
                percentage == 100 -> R.color.green_primary
                percentage >= 80 -> R.color.green_dark
                percentage >= 60 -> R.color.yellow_primary
                percentage >= 40 -> R.color.orange_dark
                else -> R.color.red_primary
            }

            binding.textViewPercentage.setTextColor(ContextCompat.getColor(requireContext(), colorResId))



            val feedback = when {
                percentage == 100 -> "Perfeito! Você dominou o quiz!"
                percentage >= 80 -> "Excelente! Ótimo trabalho!"
                percentage >= 60 -> "Muito bom! Quase lá!"
                percentage >= 40 -> "Bom começo! Continue praticando!"
                else -> "Não desanime! Revise e tente novamente."
            }
            binding.textViewFeedback.text = feedback

        } else {
            binding.textViewPercentage.text = "N/A"
            binding.textViewFeedback.text = "Não foi possível calcular o resultado. Nenhuma pergunta."
            binding.textViewPercentage.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }


        binding.buttonPlayAgain.visibility = View.GONE
    }

    private fun setupListeners() {

        binding.buttonRetry.setOnClickListener {

            val themeId = args.themeId
            val isCustomTheme = args.isCustomTheme


            val action = ResultFragmentDirections.actionResultFragmentToQuizFragment(themeId, isCustomTheme)
            findNavController().navigate(action)
        }


        binding.buttonHome.setOnClickListener {

            findNavController().navigate(R.id.action_resultFragment_to_themeListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}