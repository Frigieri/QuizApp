package com.example.quizapp.data.repository

import com.example.quizapp.data.model.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject // Hilt/Dagger
import javax.inject.Singleton //Hilt/Dagger

@Singleton //Hilt/Dagger
class AuthRepository @Inject constructor( //Hilt/Dagger
    private val firebaseAuth: FirebaseAuth
) {

    // Se não usar Hilt/Dagger, instancie diretamente:
    // constructor() : this(FirebaseAuth.getInstance())

    fun getCurrentUser() = firebaseAuth.currentUser

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    suspend fun register(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // Salvar nome de usuário no Firestore ou ProfileUpdates aqui
            AuthResult.Success(result.user)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}

