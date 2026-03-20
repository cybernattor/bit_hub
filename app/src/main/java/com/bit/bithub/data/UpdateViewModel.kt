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
import com.g00fy2.versioncompare.Version
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
                try {
                    val currentVersion = Version(BuildConfig.VERSION_NAME)
                    val latestVersion = Version(latestRelease.versionName)
                    
                    if (latestVersion.isHigherThan(currentVersion)) {
                        updateInfo = latestRelease
                    }
                } catch (e: Exception) {
                    // Fallback to string comparison if version parsing fails
                    if (latestRelease.versionName != BuildConfig.VERSION_NAME) {
                        updateInfo = latestRelease
                    }
                }
            }
            isChecking = false
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
