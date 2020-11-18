package com.safefleet.lawmobile.helpers

import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.lawmobile.presentation.ui.login.LoginActivity

class DeviceUtils {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    fun switchToLandscape() = device.setOrientationLeft()

    fun switchToPortrait() = device.setOrientationNatural()

    fun restartApp() {
        Espresso.pressBackUnconditionally()
        ActivityTestRule(LoginActivity::class.java).launchActivity(null)
    }
}
