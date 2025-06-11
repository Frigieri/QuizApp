package com.example.quizapp.data.repository

import com.example.quizapp.data.model.Question
import com.example.quizapp.data.model.Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log //

@Singleton
class QuizRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Coleções do Firestore
    private val themesCollection = firestore.collection("themes")
    private val questionsCollection = firestore.collection("questions")
    private val resultsCollection = firestore.collection("results")
    private val customThemesCollection = firestore.collection("custom_themes")
    private val customQuestionsCollection = firestore.collection("custom_questions")

    // Método para obter o ID do usuário atual
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Método para deslogar o usuário
    fun logout() {
        auth.signOut()
    }

    // Temas padrão
    suspend fun getDefaultThemes(): List<Theme> {
        return try {
            val snapshot = themesCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Theme::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error getting default themes: ${e.message}", e)
            emptyList()
        }
    }

    // Temas personalizados do usuário
    suspend fun getCustomThemes(userId: String): List<Theme> {
        return try {
            val snapshot = customThemesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Theme::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error getting custom themes for user $userId: ${e.message}", e)
            emptyList()
        }
    }

    // Perguntas para um tema específico
    suspend fun getQuestionsByTheme(themeId: String, isCustomTheme: Boolean): List<Question> {
        return try {
            val targetCollection = if (isCustomTheme) {
                customQuestionsCollection
            } else {
                questionsCollection
            }
            Log.d("QuizRepository", "Consultando Firestore na coleção: ${targetCollection.id} com themeId: $themeId (isCustomTheme: $isCustomTheme)")

            val snapshot = targetCollection
                .whereEqualTo("themeId", themeId) //
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.d("QuizRepository", "Consulta vazia: Nenhuma pergunta encontrada no Firestore para themeId: $themeId na coleção ${targetCollection.id}")
            } else {
                Log.d("QuizRepository", "Perguntas encontradas no Firestore: ${snapshot.size()} documentos na coleção ${targetCollection.id}")
            }

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Question::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "Erro na consulta Firestore para themeId: $themeId (isCustomTheme: $isCustomTheme): ${e.message}", e)
            emptyList()
        }
    }

    // Adicionar um tema personalizado
    suspend fun addCustomTheme(theme: Theme): String {
        return try {
            val docRef = customThemesCollection.add(theme).await()
            Log.d("QuizRepository", "Custom theme added with ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error adding custom theme: ${e.message}", e)
            throw e
        }
    }

    // Atualizar um tema personalizado
    suspend fun updateCustomTheme(theme: Theme) {
        try {
            customThemesCollection.document(theme.id).set(theme).await()
            Log.d("QuizRepository", "Custom theme updated: ${theme.id}")
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error updating custom theme ${theme.id}: ${e.message}", e)
            throw e
        }
    }

    // Excluir um tema personalizado
    suspend fun deleteCustomTheme(themeId: String) {
        try {
            // Exclui o tema
            customThemesCollection.document(themeId).delete().await()
            Log.d("QuizRepository", "Custom theme deleted: $themeId")

            // Exclui todas as perguntas associadas ao tema
            val questionsSnapshot = customQuestionsCollection
                .whereEqualTo("themeId", themeId)
                .get()
                .await()

            for (doc in questionsSnapshot.documents) {
                customQuestionsCollection.document(doc.id).delete().await()
                Log.d("QuizRepository", "Deleted custom question ${doc.id} for theme $themeId")
            }
            Log.d("QuizRepository", "All custom questions for theme $themeId deleted.")
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error deleting custom theme $themeId: ${e.message}", e)
            throw e
        }
    }

    // Adicionar uma pergunta personalizada - Não funciona
    suspend fun addCustomQuestion(question: Question): String {
        return try {
            val docRef = customQuestionsCollection.add(question).await()
            Log.d("QuizRepository", "Custom question added with ID: ${docRef.id} for theme ${question.themeId}")
            docRef.id
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error adding custom question: ${e.message}", e)
            throw e
        }
    }

    // Atualizar uma pergunta personalizada - Não funciona
    suspend fun updateCustomQuestion(question: Question) {
        try {
            customQuestionsCollection.document(question.id).set(question).await()
            Log.d("QuizRepository", "Custom question updated: ${question.id} for theme ${question.themeId}")
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error updating custom question ${question.id}: ${e.message}", e)
            throw e
        }
    }

    // Excluir uma pergunta personalizada - Não funciona
    suspend fun deleteCustomQuestion(questionId: String) {
        try {
            customQuestionsCollection.document(questionId).delete().await()
            Log.d("QuizRepository", "Custom question deleted: $questionId")
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error deleting custom question ${questionId}: ${e.message}", e)
            throw e
        }
    }

    // Salvar o resultado de um quiz - Não usado
    suspend fun saveQuizResult(userId: String, themeId: String, score: Int, totalQuestions: Int) {
        try {
            val result = hashMapOf(
                "userId" to userId,
                "themeId" to themeId,
                "score" to score,
                "totalQuestions" to totalQuestions,
                "percentage" to (score.toFloat() / totalQuestions.toFloat() * 100).toInt(),
                "timestamp" to System.currentTimeMillis()
            )

            resultsCollection.add(result).await()
            Log.d("QuizRepository", "Quiz result saved for user $userId, theme $themeId. Score: $score/$totalQuestions")
        } catch (e: Exception) {
            // Apenas registra o erro, não interrompe o fluxo
            Log.e("QuizRepository", "Error saving quiz result for user $userId, theme $themeId: ${e.message}", e)
            e.printStackTrace()
        }
    }

    // Obter histórico de resultados do usuário - Não usado
    suspend fun getUserResults(userId: String): List<Map<String, Any>> {
        return try {
            val snapshot = resultsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val results = snapshot.documents.map { doc ->
                doc.data ?: emptyMap()
            }
            Log.d("QuizRepository", "User results loaded for user $userId. Count: ${results.size}")
            results
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error getting user results for user $userId: ${e.message}", e)
            emptyList()
        }
    }
}