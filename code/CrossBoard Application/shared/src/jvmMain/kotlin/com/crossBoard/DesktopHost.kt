package com.crossBoard

/**
 *
 */
class DesktopHost: Host {
    override val address: String
        get() = "127.0.0.1"

    override val port: Int
        get() = 8000
}

actual fun getHost(): Host = DesktopHost()