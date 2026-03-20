package com.bit.bithub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bit.bithub.components.SettingsItem
import com.bit.bithub.components.SettingsSection
import com.bit.bithub.components.ThemeSelectionDialog
import com.bit.bithub.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentThemeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    downloadWifiOnly: Boolean,
    onDownloadWifiOnlyChange: (Boolean) -> Unit,
    installedCount: Int,
    onClose: () -> Unit
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentThemeMode = currentThemeMode,
            onDismiss = { showThemeDialog = false },
            onThemeSelect = {
                onThemeChange(it)
                showThemeDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Аккаунт") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(70.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Пользователь bit Hub", style = MaterialTheme.typography.headlineSmall)
            
            Spacer(Modifier.height(24.dp))
            
            SettingsSection(title = "Управление") {
                ListItem(
                    headlineContent = { Text("Мои приложения и игры") },
                    trailingContent = { Text("$installedCount шт.") },
                    leadingContent = { Icon(Icons.AutoMirrored.Filled.List, null) }
                )
            }

            SettingsSection(title = "Сеть") {
                ListItem(
                    headlineContent = { Text("Только через Wi-Fi") },
                    supportingContent = { Text("Скачивание и обновление приложений") },
                    trailingContent = {
                        Switch(
                            checked = downloadWifiOnly,
                            onCheckedChange = onDownloadWifiOnlyChange
                        )
                    },
                    leadingContent = { Icon(Icons.Default.Wifi, null) }
                )
            }

            SettingsSection(title = "Настройки") {
                SettingsItem(Icons.Default.Palette, "Тема оформления") { showThemeDialog = true }
                SettingsItem(Icons.Default.Notifications, "Уведомления") { }
            }

            SettingsSection(title = "Инфо") {
                ListItem(
                    headlineContent = { Text("Версия") },
                    trailingContent = { Text("1.6.0-stable") },
                    leadingContent = { Icon(Icons.Default.Info, null) }
                )
            }
        }
    }
}
