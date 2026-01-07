package com.laibandis.shield

import android.content.Context
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object CallGuard {

    private const val COOLDOWN_MS = 5 * 60 * 1000L // 5 минут
    private val mem = ConcurrentHashMap<String, Long>()

    private fun file(ctx: Context) = File(ctx.filesDir, "callguard.txt")

    fun load(ctx: Context) {
        val f = file(ctx)
        if (!f.exists()) return
        f.readLines().forEach { line ->
            val p = line.split("|", limit = 2)
            if (p.size == 2) mem[p[0]] = p[1].toLongOrNull() ?: 0L
        }
    }

    fun save(ctx: Context) {
        val f = file(ctx)
        f.writeText(mem.entries.joinToString("\n") { "${it.key}|${it.value}" })
    }

    fun canCall(ctx: Context, phone: String): Boolean {
        val now = System.currentTimeMillis()
        val last = mem[phone] ?: 0L
        if (now - last >= COOLDOWN_MS) {
            mem[phone] = now
            save(ctx)
            return true
        }
        return false
    }
}
