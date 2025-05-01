package com.crossBoard

import android.content.Context
import com.crossBoard.AndroidAppContext.applicationContext
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

object AndroidAppContext {
    lateinit var applicationContext: Context
        private set
    fun initialize(context: Context) { applicationContext = context.applicationContext }
}

actual fun getSettings(): Settings {
    return SharedPreferencesSettings(applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE))
}