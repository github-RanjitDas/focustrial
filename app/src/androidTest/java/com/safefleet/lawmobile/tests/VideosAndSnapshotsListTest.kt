package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.testData.CameraFilesData
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class VideosAndSnapshotsListTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {
    companion object {
        val snapshotsList = CameraFilesData.DEFAULT_SNAPSHOTS_LIST.value
        val snapshotsQuantity = snapshotsList.size

        val extraSnapshotsList = CameraFilesData.EXTRA_SNAPSHOTS_LIST.value

        val fileListScreen = FileListScreen()
        val liveViewScreen = LiveViewScreen()
    }

    @Before
    fun login() = LoginScreen().login()


    @Test
    fun verifyNoSnapshotsTaken_FMA_559() {
        mockUtils.clearSnapshotsOnX1()

        liveViewScreen.openSnapshotsList()
        fileListScreen.areNoSnapshotsFound()

        fileListScreen.goBack()

        liveViewScreen.takeSnapshot()
        liveViewScreen.takeSnapshot()
        liveViewScreen.takeSnapshot()

        liveViewScreen.openSnapshotsList()

        fileListScreen.areFilesSortedByDate(extraSnapshotsList.subList(0, 3))
    }

    @Test
    fun verifyCheckboxWhenSnapshotsDontFit_FMA_560() {
        liveViewScreen.openSnapshotsList()
        fileListScreen.areFilesSortedByDate(snapshotsList)

        fileListScreen
            .selectCheckboxOnPosition(0)
            .isCheckboxSelected(0)

        fileListScreen.areCheckboxesUnselected(
            startPosition = 1,
            endPosition = snapshotsQuantity - 1
        )

        fileListScreen
            .selectCheckboxOnPosition(14)
            .selectCheckboxOnPosition(7)
            .selectCheckboxOnPosition(0)

        fileListScreen
            .isCheckboxSelected(14)
            .isCheckboxSelected(7)
            .isCheckboxUnselected(0)

        fileListScreen.switchToVideosList()
        fileListScreen.switchToSnapshotsList()

        fileListScreen.areCheckboxesUnselected(
            startPosition = 0,
            endPosition = snapshotsQuantity - 1
        )
    }

    @Test
    fun verifyCheckboxFunctionality_FMA_561() {
        liveViewScreen.openSnapshotsList()
        fileListScreen.areFilesSortedByDate(snapshotsList)

        fileListScreen
            .selectCheckboxOnPosition(0)
            .isCheckboxSelected(0)

        fileListScreen
            .selectCheckboxOnPosition(4)
            .isCheckboxSelected(4)

        fileListScreen
            .selectCheckboxOnPosition(7)
            .isCheckboxSelected(7)

        fileListScreen
            .selectCheckboxOnPosition(0)
            .selectCheckboxOnPosition(4)
            .selectCheckboxOnPosition(7)

        fileListScreen
            .isCheckboxUnselected(0)
            .isCheckboxUnselected(4)
            .isCheckboxUnselected(7)
    }

    @Test
    fun verifyScrollWhenSnapshotsDontFit_FMA_562() {
        liveViewScreen.openSnapshotsList()

        fileListScreen.scrollListToPosition(snapshotsQuantity - 1)
        fileListScreen.scrollListToPosition(0)
        fileListScreen.scrollListToPosition(snapshotsQuantity - 1)
        fileListScreen.scrollListToPosition(0)

        fileListScreen.areFilesSortedByDate(snapshotsList)
    }

    @Test
    fun verifyUpdatingSnapshotsList_FMA_563() {
        val takenSnapshots = extraSnapshotsList.subList(0, 3)

        liveViewScreen.openSnapshotsList()
        fileListScreen.areFilesSortedByDate(snapshotsList)

        fileListScreen.goBack()
        liveViewScreen.isLiveViewDisplayed()

        liveViewScreen.takeSnapshot()
        liveViewScreen.takeSnapshot()
        liveViewScreen.takeSnapshot()

        liveViewScreen.openSnapshotsList()
        fileListScreen.areFilesSortedByDate(snapshotsList + takenSnapshots)
    }

    @Test
    fun verifyDisconnectionOpeningSnapsList_FMA_566() {
        mockUtils.disconnectCamera()

        liveViewScreen.openSnapshotsList()
        liveViewScreen.isDisconnectionAlertDisplayed()
    }

    @Test
    fun verifyDisconnectionSwitchToVideosList_FMA_567() {
        liveViewScreen.openSnapshotsList()

        mockUtils.disconnectCamera()

        fileListScreen.switchToVideosList()
        fileListScreen.isDisconnectionAlertDisplayed()
    }

    @Test
    fun verifyDisconnectionSwitchToSnapsList_FMA_567() {
        liveViewScreen.openVideosList()

        mockUtils.disconnectCamera()

        fileListScreen.switchToSnapshotsList()
        fileListScreen.isDisconnectionAlertDisplayed()
    }
}
