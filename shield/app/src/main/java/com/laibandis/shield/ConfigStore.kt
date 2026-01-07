package com.laibandis.shield

import android.content.Context
import java.io.File

object ConfigStore {
    private fun file(ctx: Context) = File(ctx.filesDir, "config.json")

    fun save(ctx: Context, json: String) {
        file(ctx).writeText(json)
    }

    fun load(ctx: Context): String? {
        val f = file(ctx)
        return if (f.exists()) f.readText() else null
    }

    fun enabled(key: String, default: Boolean = true): Boolean {
        val cfg = ShieldApp.ctx?.let { load(it) } ?: return default
        return cfg.contains("\"$key\":true") || (!cfg.contains("\"$key\"") && default)
    }
}
