package com.bit.bithub.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.bit.bithub.R

enum class AppDestinations(val labelRes: Int, val icon: ImageVector) {
    HOME(R.string.nav_home, Icons.Default.Home),
    APPS(R.string.nav_apps, Icons.Default.Apps),
    GAMES(R.string.nav_games, Icons.Default.Games)
}
