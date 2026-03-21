package com.bit.bithub

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class BitHubApplication : Application() {
    
    lateinit var supabase: SupabaseClient

    override fun onCreate() {
        super.onCreate()
        initSupabase()
        createNotificationChannel()
    }

    private fun initSupabase() {
        supabase = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                INSTALL_CHANNEL_ID,
                getString(R.string.notif_installed_title),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val INSTALL_CHANNEL_ID = "INSTALL_CHANNEL"
    }
}
