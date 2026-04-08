package com.bit.bithub.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.bit.bithub.BitHubApplication
import com.bit.bithub.R
import com.bit.bithub.data.AppItem
import com.bit.bithub.settings.SettingsManager
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val UPDATES_CHANNEL_ID = "UPDATES_CHANNEL"
        const val UPDATES_NOTIF_ID = 1001
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (!SettingsManager.periodicUpdateCheck) return@withContext Result.success()

        try {
            val appContainer = applicationContext as? BitHubApplication
            val supabase = appContainer?.supabase ?: return@withContext Result.retry()

            // Получаем список приложений из БД (заодно пингуем базу)
            val cloudApps = supabase.from("apps").select().decodeList<AppItem>()

            // Проверяем установленные приложения на наличие обновлений
            val pm = applicationContext.packageManager
            var updatesCount = 0

            for (app in cloudApps) {
                val pkgName = app.packageName ?: continue
                try {
                    val installedInfo = pm.getPackageInfo(pkgName, 0)
                    val installedVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        installedInfo.longVersionCode.toInt()
                    } else {
                        @Suppress("DEPRECATION")
                        installedInfo.versionCode
                    }
                    if (app.versionNumber > installedVersionCode) {
                        updatesCount++
                    }
                } catch (_: Exception) {
                    // Приложение не установлено — пропускаем
                }
            }

            if (updatesCount > 0) {
                sendUpdateNotification(updatesCount)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun sendUpdateNotification(count: Int) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаём канал уведомлений для обновлений (если ещё нет)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UPDATES_CHANNEL_ID,
                "Обновления приложений",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о доступных обновлениях"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val message = if (count == 1) {
            "Доступно 1 обновление"
        } else {
            "Доступно $count обновления(-й)"
        }

        val notification = NotificationCompat.Builder(applicationContext, UPDATES_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("bit Hub")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(UPDATES_NOTIF_ID, notification)
    }
}

