package com.laibandis.shield

import android.content.Context
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object BlacklistStore {

    private val set = ConcurrentHashMap.newKeySet<String>()

    private fun file(ctx: Context) = File(ctx.filesDir, "blacklist.txt")

    fun load(ctx: Context) {
        val f = file(ctx)
        if (!f.exists()) return
        f.readLines().map { it.trim() }.filter { it.isNotEmpty() }.forEach { set.add(it) }
    }

    fun save(ctx: Context) {
        file(ctx).writeText(set.joinToString("\n"))
    }

    fun replaceAll(ctx: Context, phones: List<String>) {
        set.clear()
        phones.forEach { set.add(it.trim()) }
        save(ctx)
    }

    fun contains(phone: String): Boolean = set.contains(phone.trim())
}
