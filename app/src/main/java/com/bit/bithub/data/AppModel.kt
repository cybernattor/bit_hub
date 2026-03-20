package com.bit.bithub.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class AppItem(
    val id: Int? = null,
    val title: String,
    val developer: String,
    val rating: Double,
    val reviews: String,
    val size: String,
    val description: String,
    @SerialName("icon_url") val iconUrl: String? = null,
    @SerialName("icon_color") val iconColorHex: String? = null,
    @SerialName("is_game") val isGame: Boolean = false,
    @SerialName("download_url") val downloadUrl: String? = null,
    @SerialName("package_name") val packageName: String? = null,
    @SerialName("version_code") val versionCode: String = "1.0.0",
    @SerialName("version_number") val versionNumber: Int = 0
)

val mockAppsData = listOf(
    AppItem(1, "bit Stream", "Stream Inc.", 4.5, "12K", "45MB", "Лучшее приложение для стриминга вашего контента.", "https://picsum.photos/id/1/200/200", "#2C6CFF", packageName = "com.bit.stream", versionCode = "1.2.0", versionNumber = 12),
    AppItem(2, "bit Pixel Art", "Design Studio", 4.8, "5K", "12MB", "Рисуйте пиксель-арт с легкостью.", "https://picsum.photos/id/2/200/200", "#009688", packageName = "com.bit.pixelart", versionCode = "1.0.1", versionNumber = 2),
    AppItem(3, "bit Code Runner", "Dev Tools", 4.2, "8K", "30MB", "Компилируйте код прямо на вашем смартфоне.", "https://picsum.photos/id/3/200/200", "#E91E63", packageName = "com.bit.coderunner", versionCode = "2.1.0", versionNumber = 21),
    AppItem(4, "bit Notes Plus", "Productivity", 4.7, "20K", "15MB", "Умные заметки с поддержкой облачной синхронизации.", "https://picsum.photos/id/4/200/200", "#9C27B0", packageName = "com.bit.notes", versionCode = "1.5.4", versionNumber = 15),
    AppItem(5, "bit Monster Hunter", "GameDev Hub", 4.9, "1M", "2GB", "Эпическая RPG игра про охоту на монстров.", "https://picsum.photos/id/5/300/200", "#FF9800", true, packageName = "com.bit.monsterhunter", versionCode = "1.0.0", versionNumber = 1),
    AppItem(6, "bit Racing Pro", "Speed Games", 4.6, "500K", "1.2GB", "Участвуйте в самых быстрых гонках на планете.", "https://picsum.photos/id/6/300/200", "#F44336", true, packageName = "com.bit.racingpro", versionCode = "3.2.1", versionNumber = 32),
    AppItem(7, "bit Space Puzzle", "Logic Games", 4.4, "100K", "150MB", "Сложные головоломки в декорациях глубокого космоса.", "https://picsum.photos/id/7/300/200", "#2196F3", true, packageName = "com.bit.spacepuzzle", versionCode = "1.1.0", versionNumber = 11)
)
