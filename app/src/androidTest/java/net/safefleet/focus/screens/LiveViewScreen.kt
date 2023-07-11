package net.safefleet.focus.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions.waitUntil
import net.safefleet.focus.helpers.isActivated
import net.safefleet.focus.helpers.isNotActivated

open class LiveViewScreen : BaseScreen() {

    private val mainMenuScreen = MainMenuScreen()
    private val helPageScreen = HelpPageScreen()
    private val notificationViewScreen = NotificationViewScreen()

    fun switchLiveViewToggle() = waitUntil { clickOn(R.id.buttonSwitchLiveView) }

    fun switchFullScreenMode() = waitUntil { clickOn(R.id.toggleFullScreenLiveView) }

    open fun openSnapshotList() = waitUntil { clickOn(R.id.buttonSnapshotList) }

    open fun openVideoList() = waitUntil { clickOn(R.id.buttonVideoList) }

    open fun closeErrorMessage() = clickOn(R.id.closeSnackBarButton)

    fun refreshCameraStatus() {
        waitUntil { mainMenuScreen.clickOnMainMenu() }
        mainMenuScreen.clickOnViewHelp()
        sleep(500)
    }

    fun closeHelpView() = helPageScreen.goBack()

    fun clickOkButton() = waitUntil { clickOn(R.string.OK) }

    fun isLowStorageNotificationDisplayed() {
        notificationViewScreen.isLowStorageTitleDisplayed()
        notificationViewScreen.isDateDisplayed()
        notificationViewScreen.isLowStorageDescriptionDisplayed()
        notificationViewScreen.isDismissButtonDisplayed()
        notificationViewScreen.clickOnDismissButton()
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

        isLiveViewTextDisplayed()

        assertDisplayed(R.id.buttonSnapshotList)
        assertDisplayed(R.string.view_snapshots)

        assertDisplayed(R.id.buttonVideoList)
        assertDisplayed(R.string.view_videos)
    }

    fun isLiveViewTextDisplayed() = waitUntil { assertDisplayed(R.id.textLiveViewSwitch, R.string.live_view_label) }

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
        BaristaSleepInteractions.sleep(1000)
        waitUntil {
            assertContains(R.id.textViewBatteryPercent, "$percent %")
        }
    }

    fun isBatteryIndicatorTextDisplayedX1(percent: String) {
        waitUntil {
            assertContains(R.id.textViewBatteryPercent, "$percent%")
        }
    }

    fun isBatteryIndicatorNotAvailableDisplayedX1() {
        waitUntil {
            assertContains(R.id.textViewBatteryPercent, R.string.not_available)
        }
    }

    fun isMemoryStorageIndicatorTextDisplayed(percent: String) {
        sleep(1000)
        waitUntil {
            assertContains(R.id.textViewStorageLevels, "$percent%")
        }
    }

    fun isMemoryStorageIndicatorTextDisplayedX1(storage: String) {
        waitUntil {
            assertContains(R.id.textViewStorageLevels, storage)
        }
    }

    fun isLowSignalPopUpDisplayed() {
        assertDisplayed(R.string.low_signal_title)
        assertDisplayed(R.string.low_signal_message)
    }

    fun isVideoRecordingErrorDisplayed() {
        waitUntil { assertDisplayed(R.string.error_saving_video) }
    }

    fun isStartingRecordingDisplayed() = waitUntil { assertDisplayed(R.string.starting_recording) }

    fun isVideoRecording() {
        waitUntil { assertDisplayed(R.string.video_recording) }
        waitUntil {
            assertHasDrawable(
                R.id.imageViewCustomRecord,
                R.drawable.ic_record_active
            )
        }
        waitUntil {
            assertHasDrawable(
                R.id.imageRecordingIndicator,
                R.drawable.ic_recording_circle
            )
        }
    }

    fun isVideoNotRecording() {
        waitUntil { assertNotDisplayed(R.string.video_recording) }
        waitUntil { assertHasDrawable(R.id.imageViewCustomRecord, R.drawable.ic_video) }
    }
}
