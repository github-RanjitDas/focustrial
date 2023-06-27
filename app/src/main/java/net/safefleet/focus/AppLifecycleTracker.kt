package net.safefleet.focus

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class AppLifecycleTracker : Application.ActivityLifecycleCallbacks {

    private var numStarted = 0

    override fun onActivityStarted(activity: Activity) {
        numStarted++
    }

    override fun onActivityStopped(activity: Activity) {
        numStarted--
        if (numStarted == 0) {
            val folderDirCache = activity.cacheDir
            folderDirCache.listFiles()?.forEach {
                it.delete()
            }
            folderDirCache.delete()
            folderDirCache.deleteOnExit()
            Log.i("onActivityStopped", "onActivityStopped: $numStarted")
        }
    }

    override fun onActivityPaused(activity: Activity) {
        // The interface required to implement this method but not necessary to override
    }
    override fun onActivityDestroyed(activity: Activity) {
        // The interface required to implement this method but not necessary to override
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // The interface required to implement this method but not necessary to override
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // The interface required to implement this method but not necessary to override
    }
    override fun onActivityResumed(activity: Activity) {
        // The interface required to implement this method but not necessary to override
    }
}
