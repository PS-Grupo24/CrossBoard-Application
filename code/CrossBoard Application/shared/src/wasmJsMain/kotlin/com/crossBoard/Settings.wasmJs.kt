package com.crossBoard


import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

actual fun getSettings(): Settings{
    return StorageSettings()
}
