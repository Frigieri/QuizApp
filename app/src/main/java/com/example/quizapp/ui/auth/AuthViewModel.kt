package com.example.quizapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.repository.AuthRepository
import com.example.quizapp.data.model.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel // Se usar Hilt
import kotlinx.coroutines.launch
import javax.inject.Inject // Se usar Hilt

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {



    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val result = repository.login(email, password)
            _authResult.value = result
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val result = repository.register(email, password)
            _authResult.value = result
        }
    }


    fun checkCurrentUser() {
        val user = repository.getCurrentUser()
        if (user != null) {
            _authResult.value = AuthResult.Success(user)
        } else {

        }
    }

    fun logout() {
        repository.logout()
        _authResult.value = AuthResult.Success(null)
    }
}

