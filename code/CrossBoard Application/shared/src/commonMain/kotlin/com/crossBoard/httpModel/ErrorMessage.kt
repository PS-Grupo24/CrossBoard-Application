package com.crossBoard.httpModel

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(val message: String)