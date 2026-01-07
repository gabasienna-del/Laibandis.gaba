package com.laibandis.shield

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.laibandis.core.IShield

class ShieldService : Service() {

    override fun onCreate() {
        super.onCreate()
        ShieldState.ready = true
        LogBus.i("Shield engine started")

        TaskQueue.start(this)

        // health ping
        Scheduler.every(60_000) { Health.lastPing = System.currentTimeMillis() }

        // пример: можно будет потом обновлять конфиг
        // Scheduler.every(300_000) { ConfigUpdater.run(this) }

        Scheduler.start()
    }

    override fun onBind(i: Intent): IBinder = binder

    private val binder = object : IShield.Stub() {

        override fun setToken(token: String) {
            TokenStore.save(this@ShieldService, token)
            LogBus.i("Token updated")
            EventBus.emit("TOKEN_UPDATED")
        }

        override fun request(base: String, path: String, jsonBody: String): String {
            Health.lastRequest = System.currentTimeMillis()
            val cacheKey = "$base|$path|$jsonBody"
            CacheStore.get(this@ShieldService, cacheKey)?.let { return it }

            return try {
                val token = TokenStore.load(this@ShieldService)
                val url = Router.resolve(base) + path
                val res = Http.forward(url, jsonBody, token)

                if (path.contains("/profile")) {
                    ProfileStore.save(this@ShieldService, res)
                }

                CacheStore.put(this@ShieldService, cacheKey, res)
                Health.lastPing = System.currentTimeMillis()
                res
            } catch (e: Exception) {
                TaskQueue.enqueue(this@ShieldService, QueuedTask(base, path, jsonBody))
                ShieldState.lastError = e.toString()
                LogBus.e(e.toString())
                ""
            }
        }

        override fun callPhone(phone: String) {
            val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }
}
