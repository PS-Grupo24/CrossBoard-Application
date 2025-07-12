package com.crossBoard.util

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    private const val BCRYPT_COST = 12

    /**
     * Hashes a password using bcrypt with a securely generated salt.
     * The salt is automatically included in the resulting hash string.
     *
     * @param password The plain-text password to hash.
     * @return A string containing the bcrypt hash (including cost, salt, and hash).
     */
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray())
    }

    /**
     * Verifies a plain-text password against a stored bcrypt hash.
     *
     * @param password The plain-text password from the user's login attempt.
     * @param storedHash The hash retrieved from the database for that user.
     * @return True if the password matches the hash, false otherwise.
     */
    fun checkPassword(password: String, storedHash: String): Boolean {
        // The verify method automatically extracts the salt and cost from the storedHash.
        val result = BCrypt.verifyer().verify(password.toCharArray(), storedHash)
        return result.verified
    }
}