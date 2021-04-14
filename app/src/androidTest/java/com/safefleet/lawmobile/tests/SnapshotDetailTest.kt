package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.SnapshotDetailScreen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SnapshotDetailTest : EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val fileListScreen = FileListScreen()
        private val snapshotDetailScreen = SnapshotDetailScreen()
    }

    @Before
    fun loginAndOpenSnapshot() {
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
