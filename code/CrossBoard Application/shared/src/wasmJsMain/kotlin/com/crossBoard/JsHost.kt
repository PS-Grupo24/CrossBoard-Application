package com.crossBoard

class JsHost: Host {
    override val hostname: String
        get() = "http://127.0.0.1:8000"
}
actual fun getHost(): Host = JsHost()