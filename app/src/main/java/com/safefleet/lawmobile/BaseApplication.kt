package com.safefleet.lawmobile

import android.app.Application
import com.newrelic.agent.android.NewRelic
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        this.registerActivityLifecycleCallbacks(AppLifecycleTracker())
        startNewRelic()
    }

    private fun startNewRelic() {
        if (BuildConfig.BUILD_TYPE == "release") {
            NewRelic.withApplicationToken(
                "AA861c1409d6db06bfa9b12036716403508a522b05-NRMA"
            ).start(this.applicationContext)
        }
    }
}
