package com.laibandis.shield

import java.util.concurrent.CopyOnWriteArrayList

object ActionEngine {

    private val rules = CopyOnWriteArrayList<ActionRule>()

    fun add(rule: ActionRule) {
        rules.add(rule)
    }

    fun handle(event: String, payload: String) {
        rules.forEach { r ->
            if (r.event == event && r.condition(payload)) {
                try { r.action(payload) } catch (_: Exception) {}
            }
        }
    }
}
