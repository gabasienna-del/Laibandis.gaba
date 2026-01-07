package com.laibandis.shield

object DefaultRules {
    fun load(ctx: android.content.Context) {

        ActionEngine.add(
            ActionRule(
                event = "TOKEN_UPDATED",
                condition = { true },
                action = { Telemetry.log(ctx, "TOKEN_EVENT") }
            )
        )

        // LIVE_POST → звонок (с кулдауном + чёрный список)
        ActionEngine.add(
            ActionRule(
                event = "LIVE_POST",
                condition = { phone ->
                    phone.isNotBlank() &&
                    !BlacklistStore.contains(phone) &&
                    CallGuard.canCall(ctx, phone)
                },
                action = { phone ->
                    AutoCaller.call(ctx, phone)
                    Telemetry.log(ctx, "AUTO_CALL $phone")
                }
            )
        )
    }
}
