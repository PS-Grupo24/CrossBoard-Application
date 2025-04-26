package crossBoard

class IOSHost: Host {
    override val hostname: String
        get() = TODO("Not yet implemented")
}

actual fun getHost(): Host = IOSHost()