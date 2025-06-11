package com.example.quizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.util.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _logoutResult = MutableLiveData<DataResult<Unit>>()
    val logoutResult: LiveData<DataResult<Unit>> = _logoutResult

    init {
        loadUserEmail()
    }

    private fun loadUserEmail() {
        _userEmail.value = repository.getCurrentUserId() ?: "N/A"
    }

    fun logout() {
        viewModelScope.launch {
            _logoutResult.value = DataResult.Loading
            try {
                repository.logout()
                _logoutResult.value = DataResult.Success(Unit)
            } catch (e: Exception) {
                _logoutResult.value = DataResult.Error(e)
            }
        }
    }
}
