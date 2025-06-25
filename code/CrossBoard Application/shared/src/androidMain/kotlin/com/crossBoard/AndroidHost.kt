package com.crossBoard

/**
 * Host implementation for the android platform using `10.0.2.2` as intermediary.
 * Used during development.
 */
class AndroidHost : Host {
    override val address: String
        get() = "10.0.2.2"

    override val port: Int
        get() = 8000
}

/**
 * Actual fun to get the android host.
 */
actual fun getHost(): Host = AndroidHost()