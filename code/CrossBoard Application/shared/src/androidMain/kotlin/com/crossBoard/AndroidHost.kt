package com.crossBoard

class AndroidHost : Host {
    override val hostname: String
        get() = "http://10.0.2.2:8000"
}

actual fun getHost(): Host = AndroidHost()