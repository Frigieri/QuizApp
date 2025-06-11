package com.example.quizapp.ui.ViewModels

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
import android.util.Log

@HiltViewModel
class CustomQuestionViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _questions = MutableLiveData<DataResult<List<Question>>>()
    val questions: LiveData<DataResult<List<Question>>> = _questions

    private val _addUpdateQuestionResult = MutableLiveData<DataResult<String>>()
    val addUpdateQuestionResult: LiveData<DataResult<String>> = _addUpdateQuestionResult

    private val _deleteQuestionResult = MutableLiveData<DataResult<Unit>>()
    val deleteQuestionResult: LiveData<DataResult<Unit>> = _deleteQuestionResult

    private var currentThemeId: String? = null

    fun setThemeId(themeId: String) {
        currentThemeId = themeId
        Log.d("CustomQuestionViewModel", "Theme ID set: $themeId")
    }


    fun loadQuestions(isCustomTheme: Boolean) {
        val themeId = currentThemeId
        Log.d("CustomQuestionViewModel", "loadQuestions called for themeId: $themeId, isCustomTheme: $isCustomTheme")

        if (themeId == null) {
            _questions.value = DataResult.Error(Exception("ID do tema não definido para carregar perguntas."))
            Log.e("CustomQuestionViewModel", "loadQuestions: Theme ID is null.")
            return
        }

        viewModelScope.launch {
            _questions.value = DataResult.Loading
            try {
                val loadedQuestions: List<Question> = repository.getQuestionsByTheme(themeId, isCustomTheme)

                _questions.value = DataResult.Success(loadedQuestions)
                Log.d("CustomQuestionViewModel", "Questions loaded successfully. Size: ${loadedQuestions.size} for themeId: $themeId (isCustomTheme: $isCustomTheme)")
            } catch (e: Exception) {
                _questions.value = DataResult.Error(e)
                Log.e("CustomQuestionViewModel", "Error loading questions for theme $themeId (isCustomTheme: $isCustomTheme): ${e.message}", e)
            }
        }
    }


    fun addQuestion(question: Question) {
        viewModelScope.launch {
            // CORREÇÃO AQUI: De question.text para question.questionText
            Log.d("CustomQuestionViewModel", "Adding question: ${question.questionText}")
            _addUpdateQuestionResult.value = DataResult.Loading
            try {
                val userId = repository.getCurrentUserId()
                if (userId != null && currentThemeId != null) {
                    val newQuestion = question.copy(themeId = currentThemeId!!, isCustom = true, userId = userId)
                    val questionId = repository.addCustomQuestion(newQuestion)
                    _addUpdateQuestionResult.value = DataResult.Success(questionId)
                    Log.d("CustomQuestionViewModel", "Question added successfully. ID: $questionId")
                    loadQuestions(true)
                } else {
                    _addUpdateQuestionResult.value = DataResult.Error(Exception("Usuário não autenticado ou ID do tema não definido."))
                    Log.e("CustomQuestionViewModel", "Error adding question: User not authenticated or theme ID is null.")
                }
            } catch (e: Exception) {
                _addUpdateQuestionResult.value = DataResult.Error(e)
                Log.e("CustomQuestionViewModel", "Error adding question: ${e.message}", e)
            }
        }
    }


    fun updateQuestion(question: Question) {
        viewModelScope.launch {
            // Você pode adicionar um log para question.questionText aqui também, se desejar
            Log.d("CustomQuestionViewModel", "Updating question: ${question.id} - Text: ${question.questionText}")
            _addUpdateQuestionResult.value = DataResult.Loading
            try {
                val userId = repository.getCurrentUserId()
                if (userId != null && currentThemeId != null) {
                    val updatedQuestion = question.copy(themeId = currentThemeId!!, isCustom = true, userId = userId)
                    repository.updateCustomQuestion(updatedQuestion)
                    _addUpdateQuestionResult.value = DataResult.Success(question.id)
                    Log.d("CustomQuestionViewModel", "Question updated successfully. ID: ${question.id}")
                    loadQuestions(true)
                } else {
                    _addUpdateQuestionResult.value = DataResult.Error(Exception("Usuário não autenticado ou ID do tema não definido."))
                    Log.e("CustomQuestionViewModel", "Error updating question: User not authenticated or theme ID is null.")
                }
            } catch (e: Exception) {
                _addUpdateQuestionResult.value = DataResult.Error(e)
                Log.e("CustomQuestionViewModel", "Error updating question: ${e.message}", e)
            }
        }
    }


    fun deleteQuestion(questionId: String) {
        viewModelScope.launch {
            Log.d("CustomQuestionViewModel", "Deleting question: $questionId")
            _deleteQuestionResult.value = DataResult.Loading
            try {
                repository.deleteCustomQuestion(questionId)
                _deleteQuestionResult.value = DataResult.Success(Unit)
                Log.d("CustomQuestionViewModel", "Question deleted successfully. ID: $questionId")
                loadQuestions(true)
            } catch (e: Exception) {
                _deleteQuestionResult.value = DataResult.Error(e)
                Log.e("CustomQuestionViewModel", "Error deleting question: ${e.message}", e)
            }
        }
    }
}