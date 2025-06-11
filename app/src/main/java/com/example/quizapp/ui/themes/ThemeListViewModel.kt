package com.example.quizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.model.Theme
import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // If using Hilt
class ThemeListViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _themes = MutableLiveData<DataResult<List<Theme>>>()
    val themes: LiveData<DataResult<List<Theme>>> = _themes

    init {
        loadThemes()
    }

    fun loadThemes() {
        viewModelScope.launch {
            _themes.value = DataResult.Loading
            try {
                val themesList = repository.getDefaultThemes()
                _themes.value = DataResult.Success(themesList)
            } catch (e: Exception) {
                _themes.value = DataResult.Error(e)
            }
        }
    }
}
