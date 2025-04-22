package org.example.project

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

class DesktopHost: Host {
    override val hostname: String
        get() = "http://127.0.0.1:8000"
}

actual fun getHost(): Host = DesktopHost()