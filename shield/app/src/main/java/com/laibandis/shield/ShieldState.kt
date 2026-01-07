package com.laibandis.shield

object ShieldState {
    @Volatile var ready = false
    @Volatile var lastError: String? = null
}
