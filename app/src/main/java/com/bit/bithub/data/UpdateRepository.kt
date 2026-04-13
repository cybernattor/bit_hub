package com.bit.bithub.data

import android.content.Context
import android.util.Log
import com.bit.bithub.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class UpdateRepository(private val context: Context) {
    private val TAG = "bit_hub_updater"
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun checkUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "[UpdateCheck] Started")
            val release = client.get("https://api.github.com/repos/cybernattor/bit_hub/releases/latest").body<GitHubRelease>()
            
            // Фильтрация: .apk и -release
            val apkAsset = release.assets.find { 
                it.name.endsWith(".apk") && it.name.contains("-release", ignoreCase = true) 
            } ?: release.assets.find { it.name.endsWith(".apk") }

            if (apkAsset == null) {
                Log.e(TAG, "[UpdateCheck] No suitable APK asset found")
                return@withContext null
            }

            val remoteVersionName = parseTagName(release.tagName)
            val remoteVersionCode = extractVersionCode(apkAsset.name) ?: release.id.toInt()

            if (isVersionHigher(remoteVersionCode, remoteVersionName)) {
                Log.d(TAG, "[UpdateCheck] New version found: $remoteVersionName ($remoteVersionCode)")
                return@withContext UpdateInfo(
                    versionName = remoteVersionName,
                    versionCode = remoteVersionCode,
                    changelog = release.body,
                    downloadUrl = apkAsset.downloadUrl,
                    fileName = apkAsset.name
                )
            } else {
                Log.d(TAG, "[UpdateCheck] App is up to date")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[UpdateCheck] Error: ${e.message}")
        }
        null
    }

    private fun parseTagName(tagName: String): String {
        return tagName.trim().removePrefix("v").trim()
    }

    private fun extractVersionCode(fileName: String): Int? {
        return try {
            val regex = Regex("""-(\d+)-release\.apk$""")
            regex.find(fileName)?.groupValues?.get(1)?.toInt()
        } catch (e: Exception) {
            null
        }
    }

    private fun isVersionHigher(remoteCode: Int, remoteName: String): Boolean {
        if (remoteCode > BuildConfig.VERSION_CODE) return true
        if (remoteCode == BuildConfig.VERSION_CODE) {
            return compareVersions(remoteName, BuildConfig.VERSION_NAME) > 0
        }
        return false
    }

    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = v2.split(".").map { it.toIntOrNull() ?: 0 }
        val length = maxOf(parts1.size, parts2.size)
        for (i in 0 until length) {
            val p1 = parts1.getOrElse(i) { 0 }
            val p2 = parts2.getOrElse(i) { 0 }
            if (p1 != p2) return p1.compareTo(p2)
        }
        return 0
    }

    fun getCachedUpdateFile(fileName: String): File? {
        val file = File(context.externalCacheDir, fileName)
        return if (file.exists()) file else null
    }

    fun clearOldUpdates() {
        context.externalCacheDir?.listFiles()?.forEach { file ->
            if (file.name.endsWith(".apk")) {
                file.delete()
            }
        }
    }
}
