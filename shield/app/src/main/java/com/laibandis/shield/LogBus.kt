package com.laibandis.shield

import android.util.Log

object LogBus {
    fun i(msg: String) = Log.i("SHIELD", msg)
    fun e(msg: String) = Log.e("SHIELD", msg)
}
