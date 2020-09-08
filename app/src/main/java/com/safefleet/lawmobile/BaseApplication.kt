package com.safefleet.lawmobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        this.registerActivityLifecycleCallbacks(AppLifecycleTracker())
    }
}
