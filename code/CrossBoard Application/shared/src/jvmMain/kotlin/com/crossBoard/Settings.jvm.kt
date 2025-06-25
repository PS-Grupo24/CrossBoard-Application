package com.crossBoard

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

/**
 * Actual implementation to get the `Settings` for `JVM` platform.
 * Uses `PreferencesSettings` and `Preferences.userRoot().node("com/crossBoard/Application")` as delegate.
 */
actual fun getSettings(): Settings {
    return PreferencesSettings(Preferences.userRoot().node("com/crossBoard/Application"))
}