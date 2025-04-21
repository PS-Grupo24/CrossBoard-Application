package org.example.project

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidHost : Host {
    override val hostname: String
        get() = "http://10.0.2.2:8080"
}

actual fun getHost(): Host = AndroidHost()