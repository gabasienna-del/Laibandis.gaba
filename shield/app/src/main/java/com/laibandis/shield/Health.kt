package com.laibandis.shield

object Health {
    @Volatile var lastPing: Long = 0
    @Volatile var lastRequest: Long = 0
    fun alive() = System.currentTimeMillis() - lastPing < 60_000
}
