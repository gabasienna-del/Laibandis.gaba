package com.laibandis.shield

import java.util.concurrent.atomic.AtomicLong

object Metrics {
    val requests = AtomicLong(0)
    val errors = AtomicLong(0)
    val totalLatency = AtomicLong(0)

    fun record(latencyMs: Long, ok: Boolean) {
        requests.incrementAndGet()
        totalLatency.addAndGet(latencyMs)
        if (!ok) errors.incrementAndGet()
    }

    fun avgLatency(): Long {
        val r = requests.get()
        return if (r == 0L) 0 else totalLatency.get() / r
    }
}
