package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class AppNavigationTest : EspressoStartActivityBaseTest<LoginX1Activity>(LoginX1Activity::class.java) {

    companion object {
        val liveViewScreen = LiveViewScreen()
        val fileListScreen = FileListScreen()
    }

    @Before
    fun login() {
        LoginScreen().login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-214
     */
    @Test
    fun verifyLiveViewToggleBehaviorAfterNavigation() {
        with(liveViewScreen) {
            isLiveViewDisplayed()
            isLiveViewToggleEnabled()

            switchLiveViewToggle()
            isLiveViewToggleDisabled()

            openSnapshotList()
            fileListScreen.clickOnBack()

            isLiveViewDisplayed()
            isLiveViewToggleDisabled()

            switchLiveViewToggle()
            isLiveViewToggleEnabled()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-549
     */
    @Test
    fun verifyRecordingAfterNavigation() {
        with(liveViewScreen) {
            isLiveViewDisplayed()

            startRecording()
            isRecordingInProgress()

            openSnapshotList()
            fileListScreen.clickOnBack()

            isRecordingInProgress()

            openVideoList()
            fileListScreen.clickOnBack()

            isRecordingInProgress()

            stopRecording()
            isRecordingNotInProgress()
        }
    }
}
