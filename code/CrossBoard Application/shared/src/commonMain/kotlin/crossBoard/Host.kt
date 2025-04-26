package crossBoard

interface Host{
    val hostname: String
}

expect fun getHost(): Host