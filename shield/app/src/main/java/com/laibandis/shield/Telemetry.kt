package com.laibandis.shield

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Telemetry {

    private fun file(ctx: Context) = File(ctx.filesDir, "telemetry.log")
    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun log(ctx: Context, msg: String) {
        val line = "${fmt.format(Date())} | $msg\n"
        file(ctx).appendText(line)
    }

    fun dump(ctx: Context): String =
        if (file(ctx).exists()) file(ctx).readText() else ""
}
