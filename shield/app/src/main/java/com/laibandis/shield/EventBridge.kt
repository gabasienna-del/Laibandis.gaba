package com.laibandis.shield

object EventBridge {
    fun init() {
        EventBus.subscribe { e ->
            // формат: "EVENT:payload"
            val parts = e.split(":", limit = 2)
            val name = parts[0]
            val payload = if (parts.size > 1) parts[1] else ""
            ActionEngine.handle(name, payload)
        }
    }
}
