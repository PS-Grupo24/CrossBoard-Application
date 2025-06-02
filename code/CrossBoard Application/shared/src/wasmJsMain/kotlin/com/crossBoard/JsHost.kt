package com.crossBoard

class JsHost: Host {
    override val host: String
        get() = "127.0.0.1"

    override val port: Int
        get() = 8000
}
actual fun getHost(): Host = JsHost()