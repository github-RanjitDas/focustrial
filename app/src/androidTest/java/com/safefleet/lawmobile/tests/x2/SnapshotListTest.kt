package com.safefleet.lawmobile.tests.x2

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.helpers.SmokeTest
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.SnapshotDetailScreen
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.safefleet.lawmobile.tests.x1.AppNavigationTest.Companion.fileListScreen
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Before
import org.junit.Test

class SnapshotListTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val snapshotDetailScreen = SnapshotDetailScreen()
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().loginWithoutSSO()
    }

    @SmokeTest
    @Test
    @AllowFlaky(attempts = 1)
    fun takeSnapshotSuccess() {
        liveViewScreen.openSnapshotList()
        val initialSnapshotList = 15
        fileListScreen.matchItemsCount(initialSnapshotList)
        snapshotDetailScreen.goBack()
        with(liveViewScreen) {
            takeSnapshot()
            openSnapshotList()
            fileListScreen.clickOnSimpleListButton()
            fileListScreen.matchItemsCount(initialSnapshotList + 1)
        }
    }
}
