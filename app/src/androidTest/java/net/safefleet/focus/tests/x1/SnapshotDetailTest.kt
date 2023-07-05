package net.safefleet.focus.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions
import net.safefleet.focus.screens.FileListScreen
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.screens.SnapshotDetailScreen
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SnapshotDetailTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val fileListScreen = FileListScreen()
        private val snapshotDetailScreen = SnapshotDetailScreen()
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().login()
    }

    @Before
    fun setRecyclerView() {
        fileListScreen.recyclerView = R.id.fileListRecycler
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-835
     */
    @Test
    fun openSnapshotDetailDisconnectionX1() {
        liveViewScreen.openSnapshotList()
        mockUtils.disconnectCamera()
        fileListScreen.clickOnItemInPosition(3)
        CustomAssertionActions.waitUntil {
            fileListScreen.isDisconnectionAlertDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-836
     */
    @Test
    fun backFromSnapshotDetailDisconnectionX1() {
        liveViewScreen.openSnapshotList()
        fileListScreen.clickOnItemInPosition(3)
        mockUtils.disconnectCamera()
        snapshotDetailScreen.goBack()
        snapshotDetailScreen.isDisconnectionAlertDisplayed()
    }
}
