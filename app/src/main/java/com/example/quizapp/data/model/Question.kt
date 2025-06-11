package com.example.quizapp.data.model

data class Question(
    val id: String = "",
    val themeId: String = "",
    val questionText: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val correctOptionIndex: Int = 0,
    val explanation: String = "",
    val difficulty: Int = 1,
    val isCustom: Boolean = false,
    val userId: String = "" // Não usado pois perguntas personalizadas Não esta funcionando
)