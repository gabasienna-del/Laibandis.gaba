package com.laibandis.gaba

import android.app.Activity
import android.content.*
import android.os.Bundle
import com.laibandis.core.IShield

class MainActivity : Activity() {

    private var shield: IShield? = null

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shield = IShield.Stub.asInterface(binder)

            // Пример: передаём токен (пока тестовый)
            shield?.setToken("TEST_ACCESS_TOKEN")

            // Пример: запрос к LIVE
            val res = shield?.request("LIVE", "/api/v2/ping", "{}")
            println("Shield response: $res")
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            shield = null
        }
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val i = Intent("com.laibandis.core.BIND")
        i.setPackage("com.laibandis.shield")
        bindService(i, conn, BIND_AUTO_CREATE)
    }
}
