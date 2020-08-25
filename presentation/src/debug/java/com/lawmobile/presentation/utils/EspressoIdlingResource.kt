package com.lawmobile.presentation.utils

import android.util.Log
import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    var countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        Log.d("EspressoIdlingResource", "increment")
        countingIdlingResource.increment()
        Log.d("EspressoIdlingResource", "empty: " + countingIdlingResource.isIdleNow.toString())
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            Log.d("EspressoIdlingResource", "decrement")
            countingIdlingResource.decrement()
            Log.d("EspressoIdlingResource", "empty: " + countingIdlingResource.isIdleNow.toString())
        }
    }
}