package com.laibandis.shield

import kotlin.concurrent.thread

object LiveWatcher {

    fun start(ctx: android.content.Context) {
        thread {
            while (true) {
                try {
                    val token = TokenStore.load(ctx) ?: continue
                    val res = Http.forward(
                        "https://icl-gw-cf.euce1.laibandisapp.com/api/v2/orders/new",
                        "{}",
                        token
                    )
                    if (res.contains("phone")) {
                        val phone = Regex("\"phone\":\"(.*?)\"").find(res)?.groupValues?.get(1)
                        phone?.let { AutoCaller.call(ctx, it) }
                    }
                } catch (e: Exception) { }
                Thread.sleep(4000)
            }
        }
    }
}
