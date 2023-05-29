package com.safefleet.lawmobile

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.utils.CameraHelper
import com.newrelic.agent.android.NewRelic
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application(), LifecycleEventObserver {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
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

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                println("App enters background." + CameraInfo.isCameraConnected)
                if (CameraInfo.isCameraConnected) {
                    CameraInfo.isCameraConnected = false
                    CameraHelper.getInstance().disconnectCamera()
                }
            }

            else -> {}
        }
    }
}
