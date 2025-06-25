package com.crossBoard.httpModel

import kotlinx.serialization.Serializable

/**
 * Data class ErrorMessage represents the data format to be sent in an HTTP response when there is an error.
 * @param message The message describing the error that occurred.
 */
@Serializable
data class ErrorMessage(val message: String)