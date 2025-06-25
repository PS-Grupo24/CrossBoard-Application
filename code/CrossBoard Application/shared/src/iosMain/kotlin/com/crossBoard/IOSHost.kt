package com.crossBoard

/**
 * Host implementation for the IOS platform.
 * Used during development.
 */
class IOSHost: Host {
    override val address: String
        get() = TODO("Not yet implemented")
    override val port: Int = TODO()
}

/**
 * Actual fun to get the IOS host.
 */
actual fun getHost(): Host = IOSHost()