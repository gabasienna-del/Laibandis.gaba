package com.laibandis.shield

import android.content.Context

object ConfigUpdater {

    fun run(ctx: Context) {
        try {
            val token = TokenStore.load(ctx) ?: return
            val url = Router.resolve("PROFILE") + "/api/v2/shield/config"
            val res = Http.forward(url, "{}", token)
            if (res.isNotEmpty()) {
                ConfigStore.save(ctx, res)
                LogBus.i("Config updated")
                Telemetry.log(ctx, "CONFIG_UPDATED")
            }
        } catch (e: Exception) {
            Telemetry.log(ctx, "CONFIG_UPDATE_ERR ${e.message}")
        }
    }
}
