package com.safefleet.lawmobile.tests.x2

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoBaseTest
import com.safefleet.lawmobile.tests.x1.AppNavigationTest.Companion.fileListScreen
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
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

        liveViewScreen.startRecording()
        waitUntil { assertDisplayed(R.string.starting_recording) }
        waitUntil { assertDisplayed(R.string.video_recording) }
        waitUntil { assertHasDrawable(R.id.imageViewCustomRecord, R.drawable.ic_record_active) }
        waitUntil {
            assertHasDrawable(
                R.id.imageRecordingIndicator,
                R.drawable.ic_recording_circle
            )
        }

        liveViewScreen.stopRecording()
        waitUntil { assertNotDisplayed(R.string.video_recording) }
        waitUntil { assertHasDrawable(R.id.imageViewCustomRecord, R.drawable.ic_video) }

        liveViewScreen.openVideoList()
        fileListScreen.matchItemsCount(1)
    }
}
