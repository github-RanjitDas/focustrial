package com.safefleet.lawmobile.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.isActivated
import com.safefleet.lawmobile.helpers.isNotActivated
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep

open class LiveViewScreen : BaseScreen() {

    private val mainMenuScreen = MainMenuScreen()
    private val helPageScreen = HelpPageScreen()

    fun switchLiveViewToggle() = clickOn(R.id.buttonSwitchLiveView)

    fun switchFullScreenMode() = waitUntil { clickOn(R.id.toggleFullScreenLiveView) }

    open fun openSnapshotList() = waitUntil { clickOn(R.id.buttonSnapshotList) }

    open fun openVideoList() = waitUntil { clickOn(R.id.buttonVideoList) }

    fun openHelpPage() = clickOn(R.id.buttonOpenHelpPage)

    fun refreshCameraStatus() {
        mainMenuScreen.clickOnMainMenu()
        mainMenuScreen.clickOnViewHelp()
        sleep(500)
        helPageScreen.goBack()
    }

    open fun takeSnapshot() {
        waitUntil { clickOn(R.id.buttonSnapshot) }
        waitUntil { assertDisplayed(R.string.live_view_take_photo_success) }
        waitUntil { assertNotExist(R.string.live_view_take_photo_success) }
    }

    open fun startRecording() {
        isRecordingNotInProgress()
        clickOn(R.id.buttonRecord)
    }

    open fun stopRecording() {
        waitUntil { isRecordingInProgress() }
        clickOn(R.id.buttonRecord)
        waitUntil { isRecordingNotInProgress() }
    }

    fun isLiveViewNotDisplayed() {
        assertNotExist(R.id.buttonSwitchLiveView)
        assertNotExist(R.id.liveStreamingView)
        assertNotExist(R.id.buttonRecord)
        assertNotExist(R.id.buttonSnapshot)
        assertNotExist(R.id.buttonOpenHelpPage)
    }

    fun isBatteryStatusDisplayed() {
        assertDisplayed(R.id.imageViewBattery)
        assertDisplayed(R.id.textViewBatteryPercent)
        assertDisplayed(R.id.progressBatteryLevel)
    }

    fun isMemoryStorageStatusDisplayed() {
        assertDisplayed(R.id.imageViewStorage)
        assertDisplayed(R.id.textViewStorageLevels)
        assertDisplayed(R.id.progressStorageLevel)
    }

    fun isLiveViewDisplayed() {
        waitUntil(5000) { assertDisplayed(R.id.liveStreamingView) }
        assertDisplayed(R.id.toggleFullScreenLiveView)

        isBatteryStatusDisplayed()
        isMemoryStorageStatusDisplayed()

        assertDisplayed(R.id.buttonSnapshot)
        assertDisplayed(R.string.take_snapshots)

        assertDisplayed(R.id.buttonRecord)
        assertDisplayed(R.string.record_video)
        assertDisplayed(R.id.textLiveViewSwitch, R.string.live_view_label)

        assertDisplayed(R.id.buttonSnapshotList)
        assertDisplayed(R.string.view_snapshots)

        assertDisplayed(R.id.buttonVideoList)
        assertDisplayed(R.string.view_videos)
    }

    fun isVideoInFullScreen() {
        assertNotDisplayed(R.id.buttonSwitchLiveView)
        assertNotDisplayed(R.id.buttonRecord)
        assertNotDisplayed(R.id.buttonSnapshot)

        assertDisplayed(R.id.liveStreamingView)
        assertDisplayed(R.id.toggleFullScreenLiveView)
    }

    fun isLiveViewToggleDisabled() {
        onView(withId(R.id.buttonSwitchLiveView)).check(matches(isNotActivated()))
    }

    fun isLiveViewToggleEnabled() {
        onView(withId(R.id.buttonSwitchLiveView)).check(matches(isActivated()))
    }

    fun isRecordingNotInProgress() {
        waitUntil { onView(withId(R.id.buttonRecord)).check(matches(isNotActivated())) }
        assertNotDisplayed(R.id.textLiveViewRecording)
    }

    fun isRecordingInProgress() {
        onView(withId(R.id.buttonRecord)).check(matches(isActivated()))
        waitUntil { assertDisplayed(R.id.textLiveViewRecording) }
    }

    fun isTextBatteryIndicatorContained(text: String) {
        waitUntil { assertContains(R.id.textViewBatteryPercent, text) }
    }

    fun isBatteryIndicatorTextDisplayed(percent: String) {
        waitUntil {
            assertContains(R.id.textViewBatteryPercent, "$percent %")
        }
    }

    fun isMemoryStorageIndicatorTextDisplayed(percent: String) {
        waitUntil {
            assertContains(R.id.textViewStorageLevels, "$percent% used")
        }
    }
}
