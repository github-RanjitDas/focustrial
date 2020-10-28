package com.lawmobile.presentation.utils

import android.util.Log
import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    var countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
        Log.d("EspressoIdlingResource", countingIdlingResource.isIdleNow.toString())
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
        Log.d("EspressoIdlingResource", countingIdlingResource.isIdleNow.toString())
    }
}