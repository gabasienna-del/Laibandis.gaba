package com.laibandis.shield

object EventReactor {

    fun init() {
        EventBus.subscribe { event ->
            when (event) {
                "TOKEN_UPDATED" -> onTokenUpdated()
                "PROFILE_UPDATED" -> onProfileUpdated()
                "CONFIG_UPDATED" -> onConfigUpdated()
                else -> LogBus.i("Unhandled event: $event")
            }
        }
    }

    private fun onTokenUpdated() {
        LogBus.i("Reactor: token updated")
        // Здесь можно добавить действия: сброс кэша, переподключение и т.п.
    }

    private fun onProfileUpdated() {
        LogBus.i("Reactor: profile updated")
    }

    private fun onConfigUpdated() {
        LogBus.i("Reactor: config updated")
    }
}
