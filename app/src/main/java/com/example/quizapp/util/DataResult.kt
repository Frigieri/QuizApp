package com.example.quizapp.util

/**
 * Uma classe selada genérica para representar o resultado de uma operação,
 * especialmente útil para chamadas de rede ou banco de dados.
 */
sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val exception: Exception) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()
}
