package com.soberlock

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.android.gms.ads.MobileAds
import com.soberlock.data.database.AppDatabase

class SoberLockApplication : Application() {
    
    val database by lazy { AppDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        MobileAds.initialize(this) {}
        
        // Initialize WorkManager
        WorkManager.initialize(
            this,
            Configuration.Builder().build()
        )
    }
}