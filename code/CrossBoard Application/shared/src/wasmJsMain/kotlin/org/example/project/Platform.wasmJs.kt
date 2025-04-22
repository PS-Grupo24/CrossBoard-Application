package org.example.project

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

class JsHost: Host {
    override val hostname: String
        get() = "http://127.0.0.1:8000"
}
actual fun getHost(): Host = JsHost()