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
    }

    override fun onBind(i: Intent): IBinder = binder

    private val binder = object : IShield.Stub() {

        override fun setToken(token: String) {
            TokenStore.save(this@ShieldService, token)
            LogBus.i("Token updated")
            EventBus.emit("TOKEN_UPDATED")
        }

        override fun request(base: String, path: String, jsonBody: String): String {
            val token = TokenStore.load(this@ShieldService)
            return try {
                val baseUrl = when(base){
                    "AUTH"->"https://cas-gw-cf.euce1.laibandisapp.com"
                    "PROFILE"->"https://pf-gw-cf.euce1.laibandisapp.com"
                    "LIVE"->"https://icl-gw-cf.euce1.laibandisapp.com"
                    else->error("Unknown base")
                }
                Http.forward(baseUrl+path, jsonBody, token)
            } catch (e: Exception) {
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
