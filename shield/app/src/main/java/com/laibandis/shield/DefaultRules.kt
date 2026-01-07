package com.laibandis.shield

object DefaultRules {
    fun load(ctx: android.content.Context) {

        // Когда обновился токен — пример реакции
        ActionEngine.add(
            ActionRule(
                event = "TOKEN_UPDATED",
                condition = { true },
                action = { Telemetry.log(ctx, "TOKEN_EVENT") }
            )
        )

        // Когда LIVE прислал POST с phone → позвонить
        ActionEngine.add(
            ActionRule(
                event = "LIVE_POST",
                condition = { payload -> payload.isNotBlank() },
                action = { phone ->
                    AutoCaller.call(ctx, phone)
                }
            )
        )
    }
}
