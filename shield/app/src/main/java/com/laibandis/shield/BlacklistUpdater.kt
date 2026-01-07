package com.laibandis.shield

import android.content.Context
import org.json.JSONArray

object BlacklistUpdater {

    fun run(ctx: Context) {
        try {
            val token = TokenStore.load(ctx) ?: return
            val url = Router.resolve("PROFILE") + "/api/v2/shield/blacklist"
            val res = Http.forward(url, "{}", token)
            if (res.isNotEmpty()) {
                val arr = JSONArray(res)
                val list = mutableListOf<String>()
                for (i in 0 until arr.length()) list.add(arr.getString(i))
                BlacklistStore.replaceAll(ctx, list)
                Telemetry.log(ctx, "BLACKLIST_UPDATED size=${list.size}")
                LogBus.i("Blacklist updated: ${list.size}")
            }
        } catch (e: Exception) {
            Telemetry.log(ctx, "BLACKLIST_UPDATE_ERR ${e.message}")
        }
    }
}
