package com.laibandis.shield

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object Http {
    private val client = OkHttpClient()
    fun forward(url: String, json: String, token: String?): String {
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val req = Request.Builder().url(url).post(body)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("User-Agent", "okhttp/4.12.0").build()
        client.newCall(req).execute().use { return it.body?.string() ?: "" }
    }
}
