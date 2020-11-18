package com.safefleet.lawmobile.helpers

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

class DeviceUtils {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    fun switchToLandscape() = device.setOrientationLeft()

    fun switchToPortrait() = device.setOrientationNatural()

}
