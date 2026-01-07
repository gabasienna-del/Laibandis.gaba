package com.laibandis.shield

import android.content.Context

object ProfileUpdater {

    fun run(ctx: Context) {
        try {
            val token = TokenStore.load(ctx) ?: return
            val url = Router.resolve("PROFILE") + "/api/v2/profile"
            val res = Http.forward(url, "{}", token)
            if (res.isNotEmpty()) {
                ProfileStore.save(ctx, res)
                EventBus.emit("PROFILE_UPDATED")
                Telemetry.log(ctx, "PROFILE_UPDATED")
                LogBus.i("Profile updated")
            }
        } catch (e: Exception) {
            Telemetry.log(ctx, "PROFILE_UPDATE_ERR ${e.message}")
        }
    }
}
