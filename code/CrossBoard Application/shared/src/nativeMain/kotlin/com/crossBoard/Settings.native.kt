package com.crossBoard

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

/**
 * Actual implementation to get the `Settings` for `Native` platform.
 * Uses `NSUserDefaultSettings` and `NSUserDefaults.standardUserDefaults` as delegate.
 */
actual fun getSettings(): Settings {
    return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}