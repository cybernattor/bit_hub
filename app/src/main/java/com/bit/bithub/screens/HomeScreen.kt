package com.bit.bithub.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bit.bithub.components.*
import com.bit.bithub.data.AppItem

val homeCategories = listOf(
    "\uD83C\uDFAE" to "Игры",
    "\uD83D\uDCF1" to "Связь",
    "\uD83D\uDE80" to "Развлечения",
    "\uD83D\uDCDD" to "Инструменты"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppItem>,
    onAppClick: (AppItem) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val featured = apps.take(3)
    val recommended = apps.filter { !it.isGame }.take(5)
    val latestGames = apps.filter { it.isGame }.reversed().take(5)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("bit Hub", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, null) }
                    IconButton(onClick = onProfileClick) { Icon(Icons.Default.NotificationsNone, null) }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                val pagerState = rememberPagerState(pageCount = { featured.size })
                HomeCarousel(featured, pagerState, onAppClick)
            }

            item {
                CategoriesSection(homeCategories, onCategoryClick = {})
            }

            item {
                WideAppSection("Выбор редакции", recommended, onAppClick)
            }

            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(
                        "События и новости",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            NewsCard(
                                title = "Летняя распродажа игр",
                                description = "Скидки до 90% на хиты этого сезона",
                                imageUrl = "https://picsum.photos/id/10/600/400",
                                onClick = {}
                            )
                        }
                        item {
                            NewsCard(
                                title = "Обновление bit Stream",
                                description = "Теперь с поддержкой 4K и HDR",
                                imageUrl = "https://picsum.photos/id/20/600/400",
                                onClick = {}
                            )
                        }
                    }
                }
            }

            item {
                AppSection("Новинки гейминга", latestGames, onAppClick)
            }
        }
    }
}
