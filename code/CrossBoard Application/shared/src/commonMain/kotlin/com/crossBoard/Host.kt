package com.crossBoard

/**
 * Interface `Host` containing the `Address` and `Port`.
 */
interface Host{
    val address: String
    val port: Int
}

/**
 * General function scheme to get the `Host` for each platform during development.
 */
expect fun getHost(): Host