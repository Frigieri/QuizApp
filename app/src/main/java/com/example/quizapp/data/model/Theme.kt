package com.example.quizapp.data.model

import com.google.firebase.firestore.PropertyName

data class Theme(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val userId: String = "", // ID do usuário que criou o tema (para temas personalizados)
    @get:PropertyName("custom")
    val isCustom: Boolean = false // Indica se é um tema padrão ou personalizado
)
