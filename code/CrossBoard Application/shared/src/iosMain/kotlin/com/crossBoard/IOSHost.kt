package com.crossBoard

class IOSHost: Host {
    override val host: String
        get() = TODO("Not yet implemented")
    override val port: Int = TODO()
}

actual fun getHost(): Host = IOSHost()