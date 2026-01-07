package com.laibandis.shield

import android.content.Context
import org.json.JSONObject
import java.io.File

object ProfileStore {

    private fun file(ctx: Context) = File(ctx.filesDir, "profile.json")

    fun save(ctx: Context, json: String) {
        file(ctx).writeText(json)
    }

    fun load(ctx: Context): String? {
        val f = file(ctx)
        return if (f.exists()) f.readText() else null
    }

    fun getField(ctx: Context, key: String): String? {
        return load(ctx)?.let { JSONObject(it).optString(key, null) }
    }
}
