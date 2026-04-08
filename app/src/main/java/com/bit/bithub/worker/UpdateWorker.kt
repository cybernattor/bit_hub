package com.bit.bithub.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.bit.bithub.BitHubApplication
import com.bit.bithub.settings.SettingsManager
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (!SettingsManager.periodicUpdateCheck) return@withContext Result.success()

        try {
            val appContainer = applicationContext as? BitHubApplication
            val supabase = appContainer?.supabase
            
            if (supabase != null) {
                // Пинг базы данных для предотвращения паузы на бесплатном плане
                supabase.from("apps").select {
                    limit(1)
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
