package com.safefleet.lawmobile.tests.x2

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.SmokeTest
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.SnapshotDetailScreen
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.lawmobile.tests.EspressoBaseTest
import com.safefleet.lawmobile.tests.x1.AppNavigationTest.Companion.fileListScreen
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SnapshotListTest : EspressoBaseTest() {
    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val snapshotDetailScreen = SnapshotDetailScreen()

        private val extraSnapshotsList = CameraFilesData.EXTRA_SNAPSHOT_LIST.value
    }

    @get:Rule
    var baristRule = BaristaRule.create(LoginX2Activity::class.java)

    @Before
    fun setUp() {
        baristRule.launchActivity()
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().loginWithoutSSO()
    }

    @SmokeTest
    @Test
    fun takeSnapshotSuccess() {
        liveViewScreen.openSnapshotList()
        val initialSnapshotList = 15
        fileListScreen.matchItemsCount(initialSnapshotList)
        snapshotDetailScreen.goBack()
        with(liveViewScreen) {
            takeSnapshot()
            waitUntil { assertDisplayed(R.string.live_view_take_photo_success) }
            waitUntil { assertNotExist(R.string.live_view_take_photo_success) }
            openSnapshotList()
            fileListScreen.clickOnSimpleListButton()
            fileListScreen.matchItemsCount(initialSnapshotList + 1)
        }
    }
}
