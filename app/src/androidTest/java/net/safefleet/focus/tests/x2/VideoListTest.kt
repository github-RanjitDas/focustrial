package net.safefleet.focus.tests.x2

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.helpers.MockUtils.Companion.bodyCameraServiceMock
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import net.safefleet.focus.tests.x1.AppNavigationTest.Companion.fileListScreen
import org.junit.Before
import org.junit.Test

class VideoListTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        private val loginScreen = LoginScreen()
        private val liveViewScreen = LiveViewScreen()
    }

    @Before
    fun setup() {
        mockUtils.setCameraType(CameraType.X2)
        loginScreen.loginWithoutSSO()
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun recordVideoSuccessfully() {
        mockUtils.clearVideosOnBodyCamera()

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
    @AllowFlaky(attempts = 1)
    fun recordVideoFails() {
        mockUtils.clearVideosOnBodyCamera()
        bodyCameraServiceMock.setIsRecordingVideoSuccess(false)

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
