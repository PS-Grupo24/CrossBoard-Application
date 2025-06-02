package com.crossBoard

interface Host{
    val host: String
    val port: Int
}

expect fun getHost(): Host