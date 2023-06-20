package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class AppNavigationTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        val liveViewScreen = LiveViewScreen()
        val fileListScreen = FileListScreen()
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-214
     */
    @Test
    @AllowFlaky(attempts = 2)
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
