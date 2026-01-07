package com.laibandis.shield

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.laibandis.core.IShield

class ShieldService : Service() {

    override fun onCreate() {
        super.onCreate()
        LiveWatcher.start(this)
    }

    override fun onBind(i: Intent): IBinder = binder

    private val binder = object : IShield.Stub() {

        override fun setToken(token: String) {
            TokenStore.save(this@ShieldService, token)
        }

        override fun request(base: String, path: String, jsonBody: String): String {
            val token = TokenStore.load(this@ShieldService)
            val baseUrl = when(base){
                "AUTH"->"https://cas-gw-cf.euce1.laibandisapp.com"
                "PROFILE"->"https://pf-gw-cf.euce1.laibandisapp.com"
                "LIVE"->"https://icl-gw-cf.euce1.laibandisapp.com"
                else->error("Unknown base")
            }
            return Http.forward(baseUrl+path, jsonBody, token)
        }

        override fun callPhone(phone: String) {
            AutoCaller.call(this@ShieldService, phone)
        }
    }
}
