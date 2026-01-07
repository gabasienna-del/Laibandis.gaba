package com.laibandis.shield

import android.content.Context
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object BlacklistStore {

    private const val TTL = 24 * 60 * 60 * 1000L  // 24 часа
    private val map = ConcurrentHashMap<String, Long>()

    private fun file(ctx: Context) = File(ctx.filesDir, "blacklist.txt")

    fun load(ctx: Context) {
        val f = file(ctx)
        if (!f.exists()) return
        f.readLines().forEach {
            val p = it.split("|", limit = 2)
            if (p.size == 2) map[p[0]] = p[1].toLongOrNull() ?: 0L
        }
        cleanup(ctx)
    }

    fun save(ctx: Context) {
        file(ctx).writeText(map.entries.joinToString("\n") { "${it.key}|${it.value}" })
    }

    fun replaceAll(ctx: Context, phones: List<String>) {
        val now = System.currentTimeMillis()
        map.clear()
        phones.forEach { map[it.trim()] = now }
        save(ctx)
    }

    fun contains(phone: String): Boolean {
        cleanup(null)
        return map.containsKey(phone.trim())
    }

    fun cleanup(ctx: Context?) {
        val now = System.currentTimeMillis()
        val it = map.entries.iterator()
        while (it.hasNext()) {
            val e = it.next()
            if (now - e.value >= TTL) it.remove()
        }
        ctx?.let { save(it) }
    }
}
