package com.example.quizapp.ui.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.model.Theme // Importe a classe Theme
import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomThemeViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _customThemes = MutableLiveData<DataResult<List<Theme>>>()
    val customThemes: LiveData<DataResult<List<Theme>>> = _customThemes

    private val _addThemeResult = MutableLiveData<DataResult<String>>()
    val addThemeResult: LiveData<DataResult<String>> = _addThemeResult

    private val _deleteThemeResult = MutableLiveData<DataResult<Unit>>()
    val deleteThemeResult: LiveData<DataResult<Unit>> = _deleteThemeResult

    init {
        loadCustomThemes()
    }

    fun loadCustomThemes() {
        viewModelScope.launch {
            _customThemes.value = DataResult.Loading
            try {
                val userId = repository.getCurrentUserId()
                if (userId != null) {
                    val themes = repository.getCustomThemes(userId)
                    _customThemes.value = DataResult.Success(themes)
                } else {
                    _customThemes.value = DataResult.Error(Exception("Usuário não autenticado."))
                }
            } catch (e: Exception) {
                _customThemes.value = DataResult.Error(e)
            }
        }
    }

    fun addTheme(name: String, description: String) {
        viewModelScope.launch {
            _addThemeResult.value = DataResult.Loading
            try {
                val userId = repository.getCurrentUserId()
                if (userId != null) {
                    val newTheme = Theme(name = name, description = description, userId = userId, isCustom = true)
                    val themeId = repository.addCustomTheme(newTheme)
                    _addThemeResult.value = DataResult.Success(themeId)
                } else {
                    _addThemeResult.value = DataResult.Error(Exception("Usuário não autenticado."))
                }
            } catch (e: Exception) {
                _addThemeResult.value = DataResult.Error(e)
            }
        }
    }

    fun deleteTheme(themeId: String) {
        viewModelScope.launch {
            _deleteThemeResult.value = DataResult.Loading
            try {
                repository.deleteCustomTheme(themeId)
                _deleteThemeResult.value = DataResult.Success(Unit)
            } catch (e: Exception) {
                _deleteThemeResult.value = DataResult.Error(e)
            }
        }
    }
}