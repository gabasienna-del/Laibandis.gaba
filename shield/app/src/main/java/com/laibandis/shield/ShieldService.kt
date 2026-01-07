package com.laibandis.shield

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.laibandis.core.IShield

class ShieldService : Service() {
    override fun onBind(i: Intent): IBinder = binder

    private val binder = object : IShield.Stub() {
        override fun setToken(token: String) { TokenStore.token = token }
        override fun request(base: String, path: String, jsonBody: String): String {
            val baseUrl = when(base){
                "AUTH"->"https://cas-gw-cf.euce1.laibandisapp.com"
                "PROFILE"->"https://pf-gw-cf.euce1.laibandisapp.com"
                "LIVE"->"https://icl-gw-cf.euce1.laibandisapp.com"
                else->error("Unknown base")
            }
            return Http.forward(baseUrl+path, jsonBody, TokenStore.token)
        }
        override fun callPhone(phone: String) {
            val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }
}
