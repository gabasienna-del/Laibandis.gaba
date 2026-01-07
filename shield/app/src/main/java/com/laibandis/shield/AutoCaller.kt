package com.laibandis.shield

import android.content.Context
import android.content.Intent
import android.net.Uri

object AutoCaller {
    fun call(ctx: Context, phone: String) {
        val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ctx.startActivity(i)
    }
}
