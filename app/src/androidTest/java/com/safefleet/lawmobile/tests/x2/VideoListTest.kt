package com.safefleet.lawmobile.tests.x2

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.helpers.MockUtils.Companion.cameraConnectServiceX1Mock
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoBaseTest
import com.safefleet.lawmobile.tests.x1.AppNavigationTest.Companion.fileListScreen
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VideoListTest : EspressoBaseTest() {
    companion object {
        private val loginScreen = LoginScreen()
        private val liveViewScreen = LiveViewScreen()
    }

    @get:Rule
    var baristaRule = BaristaRule.create(LoginX2Activity::class.java)

    @Before
    fun setup() {
        baristaRule.launchActivity()
        mockUtils.setCameraType(CameraType.X2)
        loginScreen.loginWithoutSSO()
    }

    @Test
    fun recordVideoSuccessfully() {
        mockUtils.clearVideosOnX1()

        with(liveViewScreen) {
            startRecording()
            isStartingRecordingDisplayed()
            isVideoRecording()

            stopRecording()
            isVideoNotRecording()

            openVideoList()
            fileListScreen.matchItemsCount(1)
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2934
     */
    @Test
    fun recordVideoFails() {
        mockUtils.clearVideosOnX1()
        cameraConnectServiceX1Mock.setIsRecordingVideoSuccess(false)

        with(liveViewScreen) {
            startRecording()
            isStartingRecordingDisplayed()
            isVideoRecordingErrorDisplayed()
            isVideoRecording()

            stopRecording()
            isVideoNotRecording()

            closeErrorMessage()
            openVideoList()
            fileListScreen.isFileListNotDisplayed()
        }
    }
}
