package com.android.flags.domain.model

data class TextTemplatesModel(
    val greetings: List<String>,
    val questionTemplates: List<String>,
    val correctAnswerTemplates: List<String>,
    val incorrectAnswerTemplates: List<String>
)
