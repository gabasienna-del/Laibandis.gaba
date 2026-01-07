package com.laibandis.shield

data class ActionRule(
    val event: String,            // имя события
    val condition: (String)->Boolean, // условие
    val action: (String)->Unit    // действие
)
