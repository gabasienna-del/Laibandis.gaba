package com.laibandis.shield

object LivePostDetector {

    // Вызывается после успешного POST в LIVE
    fun onPost(path: String, response: String) {
        // Простейшая эвристика: если в ответе есть поле phone — эмитим событие
        val phone = Regex("\"phone\"\\s*:\\s*\"(.*?)\"").find(response)?.groupValues?.get(1)
        if (!phone.isNullOrBlank()) {
            EventBus.emit("LIVE_POST:$phone")
        }
    }
}
