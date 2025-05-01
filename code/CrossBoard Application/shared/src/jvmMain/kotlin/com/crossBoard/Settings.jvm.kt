package com.crossBoard

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual fun getSettings(): Settings {
    return PreferencesSettings(Preferences.userRoot().node("com/crossBoard/Application"))
}