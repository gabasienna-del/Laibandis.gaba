package com.laibandis.shield

import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

data class Job(val interval: Long, val action: ()->Unit)

object Scheduler {

    private val jobs = CopyOnWriteArrayList<Job>()
    private var started = false

    fun every(ms: Long, action: ()->Unit) {
        jobs.add(Job(ms, action))
    }

    fun start() {
        if (started) return
        started = true
        thread {
            val last = HashMap<Job, Long>()
            while (true) {
                val now = System.currentTimeMillis()
                for (j in jobs) {
                    val prev = last[j] ?: 0
                    if (now - prev >= j.interval) {
                        last[j] = now
                        try { j.action() } catch (_: Exception) {}
                    }
                }
                Thread.sleep(1000)
            }
        }
    }
}
