package com.safefleet.lawmobile.helpers

import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep

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
                sleep(100)
            }
        } while (System.currentTimeMillis() < endTime)
        espressoAssertion()
    }

    fun retry(timeout: Int = 3, espressoAssertion: (() -> Unit)) {
        var startTime = 0
        val endTime = startTime + timeout

        do {
            try {
                startTime++
                sleep(1000)
                espressoAssertion()
                return
            } catch (e: Throwable) {
                e.printStackTrace()
                sleep(100)
            }
        } while (startTime < endTime)
        espressoAssertion()
    }

    fun customSwipeRight(): ViewAction {
        return GeneralSwipeAction(
            Swipe.FAST,
            GeneralLocation.CENTER_LEFT,
            GeneralLocation.CENTER_RIGHT,
            Press.FINGER
        )
    }
}
