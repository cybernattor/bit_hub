package com.bit.bithub.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bit.bithub.settings.AutoUpdateMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoUpdateSettingsScreen(
    currentMode: AutoUpdateMode,
    onModeChange: (AutoUpdateMode) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Автообновление приложений") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            AutoUpdateOption(
                title = "Обновление через Wi-Fi или мобильный интернет",
                description = "Обновлять все приложения по мере выпуска обновлений. Может взиматься плата за передачу данных.",
                selected = currentMode == AutoUpdateMode.ANY_NETWORK,
                onClick = { onModeChange(AutoUpdateMode.ANY_NETWORK) }
            )
            
            AutoUpdateOption(
                title = "Обновление с ограниченным расходом трафика",
                description = "Если сеть Wi-Fi недоступна, bit Hub будет обновлять самые важные приложения, используя ограниченный объем трафика. Подробнее...",
                selected = currentMode == AutoUpdateMode.LIMITED_DATA,
                onClick = { onModeChange(AutoUpdateMode.LIMITED_DATA) }
            )
            
            AutoUpdateOption(
                title = "Обновление только через Wi-Fi",
                selected = currentMode == AutoUpdateMode.WIFI_ONLY,
                onClick = { onModeChange(AutoUpdateMode.WIFI_ONLY) }
            )
            
            AutoUpdateOption(
                title = "Никогда",
                selected = currentMode == AutoUpdateMode.NEVER,
                onClick = { onModeChange(AutoUpdateMode.NEVER) }
            )
        }
    }
}

@Composable
private fun AutoUpdateOption(
    title: String,
    description: String? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
            if (description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}
