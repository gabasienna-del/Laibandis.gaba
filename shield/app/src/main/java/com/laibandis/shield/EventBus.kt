package com.laibandis.shield

import java.util.concurrent.CopyOnWriteArrayList

object EventBus {
    private val listeners = CopyOnWriteArrayList<(String)->Unit>()

    fun subscribe(l:(String)->Unit) {
        listeners.add(l)
    }

    fun emit(event:String) {
        listeners.forEach { it(event) }
    }
}
