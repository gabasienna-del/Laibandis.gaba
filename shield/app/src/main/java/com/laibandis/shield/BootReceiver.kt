package com.laibandis.shield

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, i: Intent) {
        ctx.startService(Intent(ctx, ShieldService::class.java))
    }
}
