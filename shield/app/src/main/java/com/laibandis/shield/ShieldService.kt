package com.laibandis.shield

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.laibandis.core.IShield
import org.json.JSONObject

class ShieldService : Service() {

    override fun onCreate() {
        super.onCreate()
        ShieldState.ready = true
        LogBus.i("Shield autonomous engine started")

        EventReactor.init()
        EventBridge.init()
        DefaultRules.load(this)
        BlacklistStore.load(this)
        CallGuard.load(this)

        TaskQueue.start(this)

        // системный heartbeat
        Scheduler.every(60_000) { Health.lastPing = System.currentTimeMillis() }
        Scheduler.every(24 * 60 * 60 * 1000L) { BlacklistUpdater.run(this) }
        Scheduler.every(24 * 60 * 60 * 1000L) { BlacklistStore.cleanup(this) }
        Scheduler.start()
    }

    override fun onBind(i: Intent): IBinder = binder

    private val binder = object : IShield.Stub() {

        override fun setToken(token: String) {
            TokenStore.save(this@ShieldService, token)
            LogBus.i("Token updated")
            Telemetry.log(this@ShieldService, "TOKEN_UPDATED")
            EventBus.emit("TOKEN_UPDATED")
        }

        override fun request(base: String, path: String, jsonBody: String): String {
            Health.lastRequest = System.currentTimeMillis()
            val cacheKey = "$base|$path|$jsonBody"
            CacheStore.get(this@ShieldService, cacheKey)?.let { return it }

            val start = System.currentTimeMillis()
            return try {
                val token = TokenStore.load(this@ShieldService)
                val url = Router.resolve(base) + path
                val res = Http.forward(url, jsonBody, token)

                // Детектор POST-событий LIVE (твой домен)
                if (base == "LIVE") {
                    LivePostDetector.onPost(path, res)
                }

                if (path.contains("/profile")) {
                    ProfileStore.save(this@ShieldService, res)
                    EventBus.emit("PROFILE_UPDATED")
                }

                CacheStore.put(this@ShieldService, cacheKey, res)
                Metrics.record(System.currentTimeMillis() - start, true)
                Telemetry.log(this@ShieldService, "OK $base$path")
                res
            } catch (e: Exception) {
                Metrics.record(System.currentTimeMillis() - start, false)
                Telemetry.log(this@ShieldService, "ERR $base$path : ${e.message}")
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

        override fun health(): String {
            val o = JSONObject()
            o.put("ready", ShieldState.ready)
            o.put("alive", Health.alive())
            o.put("requests", Metrics.requests.get())
            o.put("errors", Metrics.errors.get())
            o.put("avgLatency", Metrics.avgLatency())
            o.put("lastError", ShieldState.lastError ?: "")
            o.put("debugProxy", DebugProxyController.isEnabled())
            return o.toString()
        }

        override fun debugProxyStart() { DebugProxyController.start(this@ShieldService) }
        override fun debugProxyStop()  { DebugProxyController.stop(this@ShieldService)  }
        override fun debugProxyEnabled(): Boolean = DebugProxyController.isEnabled()
    }
}
