package com.crossBoard

class AndroidHost : Host {
    override val host: String
        get() = "10.0.2.2"

    override val port: Int
        get() = 8000
}

actual fun getHost(): Host = AndroidHost()