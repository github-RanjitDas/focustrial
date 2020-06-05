package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions

class LiveViewScreen : BaseScreen() {

    fun isLiveViewNotDisplayed() {
        assertNotExist(R.id.buttonSwitchLiveView)
        assertNotExist(R.id.liveStreamingView)
        assertNotExist(R.id.buttonStreaming)
        assertNotExist(R.id.buttonSnapshot)
    }

    fun isLiveViewDisplayed() {
        assertDisplayed(R.id.liveStreamingView)
        assertDisplayed(R.id.toggleFullScreenLiveView)

        assertDisplayed(R.id.buttonSnapshot)
        assertDisplayed(R.string.snap)

        assertDisplayed(R.id.buttonStreaming)
        assertDisplayed(R.string.record)

        assertDisplayed(R.id.textLiveViewSwitch, R.string.live_view_label)

        assertDisplayed(R.id.buttonSnapshotList)
        assertDisplayed(R.string.snapshots)

        assertDisplayed(R.id.buttonVideoList)
        assertDisplayed(R.string.videos)
    }

    fun isVideoInFullScreen() {
        assertNotDisplayed(R.id.buttonSwitchLiveView)
        assertNotDisplayed(R.id.buttonStreaming)
        assertNotDisplayed(R.id.buttonSnapshot)

        assertDisplayed(R.id.liveStreamingView)
        assertDisplayed(R.id.toggleFullScreenLiveView)
    }

    fun isLiveViewToggleDisabled() {
        assertHasBackground(R.id.buttonSwitchLiveView, R.drawable.ic_switch_off)
    }

    fun isLiveViewToggleEnabled() {
        assertHasBackground(R.id.buttonSwitchLiveView, R.drawable.ic_switch_on)
    }

    fun isRecordingNotInProgress() {
        assertHasBackground(R.id.buttonStreaming, R.drawable.ic_record_selector)
        assertNotDisplayed(R.id.imageRecordingIndicator)
    }

    fun isRecordingInProgress() {
        assertHasBackground(R.id.buttonStreaming, R.drawable.ic_record_active)
        assertDisplayed(R.id.imageRecordingIndicator)
    }

    fun switchLiveViewToggle() = clickOn(R.id.buttonSwitchLiveView)

    fun switchFullScreenMode() {
        clickOn(R.id.toggleFullScreenLiveView)
        BaristaSleepInteractions.sleep(1000)
    }

    fun openSnapshotsList() = clickOn(R.id.buttonSnapshotList)

    fun openVideosList() = clickOn(R.id.buttonVideoList)

    fun takeSnapshot() {
        clickOn(R.id.buttonSnapshot)
        toastMessage.isToastDisplayed(R.string.live_view_take_photo_success)
        toastMessage.waitUntilToastDisappears(R.string.live_view_take_photo_success)
    }

    fun startRecording() {
        this.isRecordingNotInProgress()
        clickOn(R.id.buttonStreaming)
        this.isRecordingInProgress()
    }

    fun stopRecording() {
        this.isRecordingInProgress()
        clickOn(R.id.buttonStreaming)
        this.isRecordingNotInProgress()
    }
}
