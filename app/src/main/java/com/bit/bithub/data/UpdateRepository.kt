package com.bit.bithub.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class UpdateRepository {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun getLatestRelease(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val release = client.get("https://api.github.com/repos/cybernattor/bit_hub/releases/latest").body<GitHubRelease>()
            
            val apkAsset = release.assets.find { it.name.endsWith(".apk") }
            
            if (apkAsset != null) {
                UpdateInfo(
                    versionName = release.tagName.removePrefix("v"),
                    changelog = release.body,
                    downloadUrl = apkAsset.downloadUrl
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
