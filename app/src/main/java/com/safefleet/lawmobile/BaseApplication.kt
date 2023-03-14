package com.safefleet.lawmobile

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import com.newrelic.agent.android.NewRelic
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        this.registerActivityLifecycleCallbacks(AppLifecycleTracker())
        startNewRelic()
        setupActivityListener()
    }

    private fun startNewRelic() {
        if (BuildConfig.BUILD_TYPE == "release") {
            NewRelic.withApplicationToken(
                "AA861c1409d6db06bfa9b12036716403508a522b05-NRMA"
            ).start(this.applicationContext)
        }
    }

    private fun setupActivityListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (BuildConfig.BUILD_TYPE == "release") {
                    // Disable taking screenshots.
                    activity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                }
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
