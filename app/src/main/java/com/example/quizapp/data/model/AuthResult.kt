package com.example.quizapp.data.model
import com.google.firebase.auth.FirebaseUser


sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult() // Sucesso, pode ou não retornar o usuário (ex: logout)
    data class Error(val exception: Exception) : AuthResult() // Falha com uma exceção
    object Loading : AuthResult() // Estado de carregamento
}
