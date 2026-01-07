package com.laibandis.lspagent

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Entry : IXposedHookLoadPackage {

    private val TAG = "LaibandisLSP"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "sinet.startup.inDrive" ||
            lpparam.packageName == "com.laibandis.shield") {

            Log.i(TAG, "LSP Agent attached to ${lpparam.packageName}")
        }
    }
}
