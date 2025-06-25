package com.crossBoard

/**
 * Host implementation for the `Browser` platform using `127.0.0.1`.
 * Used during development.
 */
class JsHost: Host {
    override val address: String
        get() = "127.0.0.1"

    override val port: Int
        get() = 8000
}

/**
 * Actual implementation to get the host for the `Browser` platform.
 */
actual fun getHost(): Host = JsHost()