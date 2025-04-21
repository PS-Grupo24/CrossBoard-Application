package org.example.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

interface Host{
    val hostname: String
}

expect fun getHost(): Host