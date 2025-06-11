package com.example.quizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.model.Question
import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log // <-- Importação para Log

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _questions = MutableLiveData<DataResult<List<Question>>>()
    val questions: LiveData<DataResult<List<Question>>> = _questions


    fun loadQuestions(themeId: String, isCustomTheme: Boolean) {
        Log.d("QuizViewModel", "loadQuestions called. themeId: $themeId, isCustomTheme: $isCustomTheme")
        _questions.value = DataResult.Loading
        viewModelScope.launch {
            try {

                val questionsList = repository.getQuestionsByTheme(themeId, isCustomTheme)
                _questions.value = DataResult.Success(questionsList)
                Log.d("QuizViewModel", "Questions loaded successfully. Size: ${questionsList.size}")
            } catch (e: Exception) {
                _questions.value = DataResult.Error(e)
                Log.e("QuizViewModel", "Error loading questions: ${e.message}", e)
            }
        }
    }


    fun saveQuizResult(userId: String, themeId: String, score: Int, totalQuestions: Int) {
        viewModelScope.launch {
            try {
                repository.saveQuizResult(userId, themeId, score, totalQuestions)
            } catch (e: Exception) {

                Log.e("QuizViewModel", "Error saving quiz result: ${e.message}", e)
            }
        }
    }
}