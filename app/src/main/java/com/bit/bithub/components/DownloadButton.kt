package com.bit.bithub.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DownloadButton(
    text: String,
    progress: Float?, // 0.0 to 1.0, null if not downloading
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val animatedProgress by animateFloatAsState(targetValue = progress ?: 0f, label = "DownloadProgress")
    
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        color = if (progress != null) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
        contentColor = if (progress != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .height(36.dp)
            .widthIn(min = 88.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (progress != null) {
                // Background fill animation
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(animatedProgress)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    maxLines = 1
                )
            }
        }
    }
}
