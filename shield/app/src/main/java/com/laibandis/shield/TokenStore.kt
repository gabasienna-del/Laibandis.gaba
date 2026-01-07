package com.laibandis.shield

import android.content.Context

object TokenStore {
    private const val FILE = "token.dat"

    fun save(ctx: Context, token: String) {
        ctx.openFileOutput(FILE, Context.MODE_PRIVATE).use {
            it.write(token.toByteArray())
        }
    }

    fun load(ctx: Context): String? {
        return try {
            ctx.openFileInput(FILE).readBytes().toString(Charsets.UTF_8)
        } catch (e: Exception) { null }
    }
}
