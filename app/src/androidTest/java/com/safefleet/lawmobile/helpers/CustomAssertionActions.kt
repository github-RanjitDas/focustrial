package com.safefleet.lawmobile.helpers

import com.schibsted.spain.barista.interaction.BaristaSleepInteractions

object CustomAssertionActions {

    /**
     * This method tells espresso to wait till a certain assertion runs correctly.
     * @param espressoAssertion The assert function to run until timeout is met
     * @param timeout The maximum time which espresso will wait for the assertion to run correctly
     */
    @JvmStatic
    fun waitUntil(timeout: Long = 3000, espressoAssertion: (() -> Unit)) {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + timeout

        do {
            try {
                espressoAssertion()
                return
            } catch (e: Throwable) {
                e.printStackTrace()
                BaristaSleepInteractions.sleep(100)
            }
        } while (System.currentTimeMillis() < endTime)
        espressoAssertion()
    }
}
