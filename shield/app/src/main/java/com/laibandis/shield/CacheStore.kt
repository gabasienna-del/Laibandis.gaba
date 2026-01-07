package com.laibandis.shield

import android.content.Context
import java.io.File
import java.security.MessageDigest

object CacheStore {

    private fun keyToFile(ctx: Context, key: String): File {
        val md = MessageDigest.getInstance("MD5")
            .digest(key.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return File(ctx.filesDir, "cache_$md.json")
    }

    fun put(ctx: Context, key: String, value: String) {
        keyToFile(ctx, key).writeText(value)
    }

    fun get(ctx: Context, key: String): String? {
        val f = keyToFile(ctx, key)
        return if (f.exists()) f.readText() else null
    }
}
