package net.safefleet.focus.tests.x2

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.helpers.SmokeTest
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.screens.SnapshotDetailScreen
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import net.safefleet.focus.tests.x1.AppNavigationTest.Companion.fileListScreen
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
