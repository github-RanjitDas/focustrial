package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class LiveViewScreen : BaseScreen() {

    fun isLiveViewNotDisplayed() {
        assertNotExist(R.id.buttonSwitchLiveView)
        assertNotExist(R.id.liveStreamingView)
        assertNotExist(R.id.buttonRecord)
        assertNotExist(R.id.buttonSnapshot)
        assertNotExist(R.id.buttonOpenHelpPage)
    }

    fun isLiveViewDisplayed() {
        assertDisplayed(R.id.liveStreamingView)
        assertDisplayed(R.id.toggleFullScreenLiveView)

        assertDisplayed(R.id.buttonSnapshot)
        assertDisplayed(R.string.snap)

        assertDisplayed(R.id.buttonRecord)
        assertDisplayed(R.string.record)

        assertDisplayed(R.id.buttonOpenHelpPage)

        assertDisplayed(R.id.textLiveViewSwitch, R.string.live_view_label)

        assertDisplayed(R.id.buttonSnapshotList)
        assertDisplayed(R.string.snapshots)

        assertDisplayed(R.id.buttonVideoList)
        assertDisplayed(R.string.videos)
    }

    fun isVideoInFullScreen() {
        assertNotDisplayed(R.id.buttonSwitchLiveView)
        assertNotDisplayed(R.id.buttonRecord)
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
        assertHasBackground(R.id.buttonRecord, R.drawable.ic_record)
        assertNotDisplayed(R.id.imageRecordingIndicator)
    }

    fun isRecordingInProgress() {
        assertHasBackground(R.id.buttonRecord, R.drawable.ic_record_active)
        assertDisplayed(R.id.imageRecordingIndicator)
    }

    fun isUserGuideDisplayed() {
        assertDisplayed(R.id.pdfView)
    }

    fun switchLiveViewToggle() = clickOn(R.id.buttonSwitchLiveView)

    fun switchFullScreenMode() = clickOn(R.id.toggleFullScreenLiveView)

    fun openSnapshotList() = clickOn(R.id.buttonSnapshotList)

    fun openVideoList() = clickOn(R.id.buttonVideoList)

    fun openHelpPage() = clickOn(R.id.buttonOpenHelpPage)

    fun takeSnapshot() {
        clickOn(R.id.buttonSnapshot)
        toastMessage.isToastDisplayed(R.string.live_view_take_photo_success)
        toastMessage.waitUntilToastDisappears(R.string.live_view_take_photo_success)
    }

    fun startRecording() {
        this.isRecordingNotInProgress()
        clickOn(R.id.buttonRecord)
    }

    fun stopRecording() {
        this.isRecordingInProgress()
        clickOn(R.id.buttonRecord)
    }
}
