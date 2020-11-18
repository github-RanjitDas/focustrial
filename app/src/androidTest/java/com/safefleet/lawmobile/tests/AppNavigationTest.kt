package com.safefleet.lawmobile.tests


import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AppNavigationTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        val liveViewScreen = LiveViewScreen()
        val fileListScreen = FileListScreen()
    }

    @Before
    fun login() = LoginScreen().login()

    @Test
    fun verifySwitchBetweenSnapshotsAndVideosList_FMA_564() {
        liveViewScreen.openSnapshotsList()
        fileListScreen.switchToVideosList()
        // TODO: implement missing steps when videos are mocked
    }

    @Test
    fun verifyLiveViewToggleBehaviorAfterNavigation_FMA_214() {
        liveViewScreen.isLiveViewDisplayed()
        liveViewScreen.isLiveViewToggleEnabled()

        liveViewScreen.switchLiveViewToggle()
        liveViewScreen.isLiveViewToggleDisabled()

        liveViewScreen.openSnapshotsList()
        fileListScreen.goBack()

        liveViewScreen.isLiveViewDisplayed()
        liveViewScreen.isLiveViewToggleDisabled()

        liveViewScreen.switchLiveViewToggle()
        liveViewScreen.isLiveViewToggleEnabled()
    }

    @Test
    fun verifyRecordingAfterNavigation_FMA_549() {
        liveViewScreen.isLiveViewDisplayed()

        liveViewScreen.startRecording()
        liveViewScreen.isRecordingInProgress()

        liveViewScreen.openSnapshotsList()
        fileListScreen.goBack()

        liveViewScreen.isRecordingInProgress()

        liveViewScreen.openVideosList()
        fileListScreen.goBack()

        liveViewScreen.isRecordingInProgress()

        liveViewScreen.stopRecording()
        liveViewScreen.isRecordingNotInProgress()
    }

}
