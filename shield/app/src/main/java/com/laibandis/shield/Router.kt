package com.laibandis.shield

object Router {

    private val routes = mapOf(
        "AUTH" to "https://cas-gw-cf.euce1.laibandisapp.com",
        "PROFILE" to "https://pf-gw-cf.euce1.laibandisapp.com",
        "LIVE" to "https://icl-gw-cf.euce1.laibandisapp.com"
    )

    fun resolve(base: String): String =
        routes[base] ?: error("Unknown route: $base")
}
