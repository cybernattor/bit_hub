package com.bit.bithub.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.bit.bithub.R
import com.bit.bithub.ui.theme.ThemeMode

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        content()
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    ListItem(headlineContent = { Text(title) }, leadingContent = { Icon(icon, null) }, modifier = Modifier.fillMaxWidth().clickable { onClick() } )
}

@Composable
fun ThemeSelectionDialog(currentThemeMode: ThemeMode, onDismiss: () -> Unit, onThemeSelect: (ThemeMode) -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.dialog_select_theme)) }, text = {
            Column(Modifier.selectableGroup()) {
                ThemeOption(stringResource(R.string.theme_system), currentThemeMode == ThemeMode.SYSTEM) { onThemeSelect(ThemeMode.SYSTEM) }
                ThemeOption(stringResource(R.string.theme_light), currentThemeMode == ThemeMode.LIGHT) { onThemeSelect(ThemeMode.LIGHT) }
                ThemeOption(stringResource(R.string.theme_dark), currentThemeMode == ThemeMode.DARK) { onThemeSelect(ThemeMode.DARK) }
            }
        }, confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) } }
    )
}

@Composable
fun ThemeOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(56.dp).selectable(selected = selected, onClick = onClick, role = Role.RadioButton).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = null)
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp))
    }
}
