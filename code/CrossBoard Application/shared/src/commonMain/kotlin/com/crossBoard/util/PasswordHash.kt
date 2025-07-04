package com.crossBoard.util
import okio.ByteString.Companion.encodeUtf8

/**
 * Hashes a password.
 * @param password The password to hash.
 */
fun hashPassword(password: String): String {
    return password.encodeUtf8().sha256().base64()
}