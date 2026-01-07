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
    }

    override fun onBind(i: Intent): IBinder = binder

    private val binder = object : IShield.Stub() {

        override fun setToken(token: String) {
            TokenStore.save(this@ShieldService, token)
            LogBus.i("Token updated")
            EventBus.emit("TOKEN_UPDATED")
        }

        override fun request(base: String, path: String, jsonBody: String): String {
            val cacheKey = "$base|$path|$jsonBody"
            // Сначала попробуем отдать из кэша
            CacheStore.get(this@ShieldService, cacheKey)?.let { return it }

            return try {
                val token = TokenStore.load(this@ShieldService)
                val url = Router.resolve(base) + path
                val res = Http.forward(url, jsonBody, token)
                CacheStore.put(this@ShieldService, cacheKey, res)
                res
            } catch (e: Exception) {
                // Если сеть упала — кладём в очередь
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
