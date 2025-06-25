package com.crossBoard


import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings


/**
 * Actual fun to get the `Settings` for `Browser` using `StorageSettings` and `localStorage` as default relegate.
 */
actual fun getSettings(): Settings{
    return StorageSettings()
}
