package com.bit.bithub.data

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bit.bithub.BuildConfig
import kotlinx.coroutines.launch
import java.io.File

class UpdateViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UpdateRepository()
    
    var updateInfo by mutableStateOf<UpdateInfo?>(null)
        private set
    
    var isChecking by mutableStateOf(false)
        private set

    fun checkForUpdates() {
        viewModelScope.launch {
            isChecking = true
            val latestRelease = repository.getLatestRelease()
            if (latestRelease != null) {
                if (isVersionHigher(BuildConfig.VERSION_NAME, latestRelease.versionName)) {
                    updateInfo = latestRelease
                }
            }
            isChecking = false
        }
    }

    private fun isVersionHigher(current: String, latest: String): Boolean {
        return try {
            val currentParts = current.split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            val latestParts = latest.split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            val maxLength = maxOf(currentParts.size, latestParts.size)
            for (i in 0 until maxLength) {
                val currentPart = currentParts.getOrElse(i) { 0 }
                val latestPart = latestParts.getOrElse(i) { 0 }
                if (latestPart > currentPart) return true
                if (latestPart < currentPart) return false
            }
            false
        } catch (e: Exception) {
            latest != current
        }
    }

    fun dismissUpdate() {
        updateInfo = null
    }

    fun startUpdate(context: Context, downloadUrl: String, versionName: String) {
        val destinationFile = File(context.externalCacheDir, "bithub_update_$versionName.apk")
        if (destinationFile.exists()) {
            destinationFile.delete()
        }

        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("bit Hub Update $versionName")
            .setDescription("Downloading update...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destinationFile))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        
        // Dismiss the sheet after starting download
        updateInfo = null
    }
}
