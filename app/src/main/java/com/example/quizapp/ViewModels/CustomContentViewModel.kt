package com.example.quizapp.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.model.Question
import com.example.quizapp.data.model.Theme
import com.example.quizapp.util.DataResult // Certifique-se de que é o util.DataResult
import com.example.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log // Adicionado para logs


sealed class CrudState {
    object Idle : CrudState()
    object Loading : CrudState()
    data class Success(val message: String? = null) : CrudState()
    data class Error(val exception: Exception) : CrudState()
}

@HiltViewModel
class CustomContentViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {


    private val _customThemes = MutableLiveData<DataResult<List<Theme>>>()
    val customThemes: LiveData<DataResult<List<Theme>>> = _customThemes

    private val _themeCrudState = MutableLiveData<CrudState>(CrudState.Idle)
    val themeCrudState: LiveData<CrudState> = _themeCrudState


    private val _customQuestions = MutableLiveData<DataResult<List<Question>>>()
    val customQuestions: LiveData<DataResult<List<Question>>> = _customQuestions

    private val _questionCrudState = MutableLiveData<CrudState>(CrudState.Idle)
    val questionCrudState: LiveData<CrudState> = _questionCrudState



    fun loadCustomThemes() {
        viewModelScope.launch {
            Log.d("CustomContentViewModel", "Loading custom themes...")
            _customThemes.value = DataResult.Loading
            try {
                val userId = repository.getCurrentUserId()
                if (userId == null) {
                    _customThemes.value = DataResult.Error(Exception("Usuário não autenticado. Não é possível carregar temas personalizados."))
                    Log.e("CustomContentViewModel", "loadCustomThemes: User not authenticated.")
                    return@launch
                }
                val themesList = repository.getCustomThemes(userId)
                _customThemes.value = DataResult.Success(themesList)
                Log.d("CustomContentViewModel", "Custom themes loaded: ${themesList.size}")
            } catch (e: Exception) {
                _customThemes.value = DataResult.Error(e)
                Log.e("CustomContentViewModel", "Error loading custom themes: ${e.message}", e)
            }
        }
    }

    fun addTheme(name: String, description: String) {
        viewModelScope.launch {
            Log.d("CustomContentViewModel", "Adding custom theme: $name")
            _themeCrudState.value = CrudState.Loading
            try {
                val userId = repository.getCurrentUserId() ?: throw Exception("Usuário não autenticado")
                val theme = Theme(
                    name = name,
                    description = description,
                    userId = userId,
                    isCustom = true
                )
                repository.addCustomTheme(theme)
                _themeCrudState.value = CrudState.Success("Tema adicionado!")
                Log.d("CustomContentViewModel", "Custom theme added successfully. Recalling loadCustomThemes().")
                loadCustomThemes() // Recarrega a lista
            } catch (e: Exception) {
                _themeCrudState.value = CrudState.Error(e)
                Log.e("CustomContentViewModel", "Error adding custom theme: ${e.message}", e)
            } finally {
                _themeCrudState.value = CrudState.Idle
            }
        }
    }

    fun updateTheme(themeId: String, name: String, description: String) {
        viewModelScope.launch {
            Log.d("CustomContentViewModel", "Updating custom theme: $themeId")
            _themeCrudState.value = CrudState.Loading
            try {
                val userId = repository.getCurrentUserId() ?: throw Exception("Usuário não autenticado")
                val theme = Theme(
                    id = themeId,
                    name = name,
                    description = description,
                    userId = userId,
                    isCustom = true
                )
                repository.updateCustomTheme(theme)
                _themeCrudState.value = CrudState.Success("Tema atualizado!")
                Log.d("CustomContentViewModel", "Custom theme updated successfully. Recalling loadCustomThemes().")
                loadCustomThemes() // Recarrega a lista
            } catch (e: Exception) {
                _themeCrudState.value = CrudState.Error(e)
                Log.e("CustomContentViewModel", "Error updating custom theme ${themeId}: ${e.message}", e)
            } finally {
                _themeCrudState.value = CrudState.Idle
            }
        }
    }

    fun deleteTheme(themeId: String) {
        viewModelScope.launch {
            Log.d("CustomContentViewModel", "Deleting custom theme: $themeId")
            _themeCrudState.value = CrudState.Loading
            try {
                repository.deleteCustomTheme(themeId)
                _themeCrudState.value = CrudState.Success("Tema excluído!")
                Log.d("CustomContentViewModel", "Custom theme deleted successfully. Recalling loadCustomThemes().")
                loadCustomThemes() // Recarrega a lista
            } catch (e: Exception) {
                _themeCrudState.value = CrudState.Error(e)
                Log.e("CustomContentViewModel", "Error deleting custom theme ${themeId}: ${e.message}", e)
            } finally {
                _themeCrudState.value = CrudState.Idle
            }
        }
    }



    fun loadCustomQuestions(themeId: String) {
        viewModelScope.launch {
            Log.d("CustomContentViewModel", "Loading custom questions for theme: $themeId")
            _customQuestions.value = DataResult.Loading
            try {
                // CORREÇÃO AQUI: Chamar getQuestionsByTheme e passar isCustomTheme = true
                val questionsList = repository.getQuestionsByTheme(themeId, true)
                _customQuestions.value = DataResult.Success(questionsList)
                Log.d("CustomContentViewModel", "Custom questions loaded: ${questionsList.size} for theme: $themeId")
            } catch (e: Exception) {
                _customQuestions.value = DataResult.Error(e)
                Log.e("CustomContentViewModel", "Error loading custom questions for theme $themeId: ${e.message}", e)
            }
        }
    }

    fun addQuestion(question: Question) {
        viewModelScope.launch {
            Log.d("CustomContentViewModel", "Adding question for theme: ${question.themeId}")
            _questionCrudState.value = CrudState.Loading
            try {
                repository.addCustomQuestion(question)
                _questionCrudState.value = CrudState.Success("Pergunta adicionada!")
                Log.d("CustomContentViewModel", "Question added successfully. Recalling loadCustomQuestions().")
                loadCustomQuestions(question.themeId) // Recarrega
            } catch (e: Exception) {
                _questionCrudState.value = CrudState.Error(e)
                Log.e("CustomContentViewModel", "Error adding question: ${e.message}", e)
            } finally {
                _questionCrudState.value = CrudState.Idle
            }
        }
    }

    fun updateQuestion(question: Question) {
        viewModelScope.launch {
            Log.d("CustomContentViewModel", "Updating question: ${question.id} for theme: ${question.themeId}")
            _questionCrudState.value = CrudState.Loading
            try {
                repository.updateCustomQuestion(question)
                _questionCrudState.value = CrudState.Success("Pergunta atualizada!")
                Log.d("CustomContentViewModel", "Question updated successfully. Recalling loadCustomQuestions().")
                loadCustomQuestions(question.themeId) // Recarrega
            } catch (e: Exception) {
                _questionCrudState.value = CrudState.Error(e)
                Log.e("CustomContentViewModel", "Error updating question ${question.id}: ${e.message}", e)
            } finally {
                _questionCrudState.value = CrudState.Idle
            }
        }
    }

    fun deleteQuestion(question: Question) {
        viewModelScope.launch {
            Log.d("CustomContentViewModel", "Deleting question: ${question.id} for theme: ${question.themeId}")
            _questionCrudState.value = CrudState.Loading
            try {
                repository.deleteCustomQuestion(question.id)
                _questionCrudState.value = CrudState.Success("Pergunta excluída!")
                Log.d("CustomContentViewModel", "Question deleted successfully. Recalling loadCustomQuestions().")
                loadCustomQuestions(question.themeId) // Recarrega
            } catch (e: Exception) {
                _questionCrudState.value = CrudState.Error(e)
                Log.e("CustomContentViewModel", "Error deleting question ${question.id}: ${e.message}", e)
            } finally {
                _questionCrudState.value = CrudState.Idle
            }
        }
    }
}