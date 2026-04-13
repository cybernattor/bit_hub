package com.bit.bithub.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bit.bithub.ui.theme.ThemeMode

/**
 * Модуль управления настройками приложения.
 * В будущем сюда можно добавить сохранение в SharedPreferences или DataStore.
 */
object SettingsManager {
    // Настройка темы
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    
    // Настройка сети и обновлений
    var autoUpdateMode by mutableStateOf(AutoUpdateMode.WIFI_ONLY)
    var downloadWifiOnly by mutableStateOf(false)
    var useMobileData by mutableStateOf(true)
    var updateOverMobileData by mutableStateOf(false)
    var periodicUpdateCheck by mutableStateOf(true)
}

enum class AutoUpdateMode {
    ANY_NETWORK,
    LIMITED_DATA,
    WIFI_ONLY,
    NEVER
}
