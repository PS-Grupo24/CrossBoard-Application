package com.crossBoard

import android.content.Context
import com.crossBoard.AndroidAppContext.applicationContext
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings


/**
 * Helper to store the application context of the `Android` platform.
 */
object AndroidAppContext {
    lateinit var applicationContext: Context
        private set
    fun initialize(context: Context) { applicationContext = context.applicationContext }
}

/**
 * Gets the actual Settings for the `Android` platform.
 * Uses `SharedPreferencesSettings` and `Context.getSharedPreferences("settings", Context.MODE_PRIVATE)` as delegate.
 */
actual fun getSettings(): Settings {
    return SharedPreferencesSettings(applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE))
}