package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.testData.CameraFilesData
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class SnapshotListTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {
    companion object {
        private val snapshotsList = CameraFilesData.DEFAULT_SNAPSHOT_LIST.value
        private val snapshotsQuantity = snapshotsList.items.size

        private val extraSnapshotsList = CameraFilesData.EXTRA_SNAPSHOT_LIST.value

        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
    }

    @Before
    fun login() = LoginScreen().login()

    private fun setSimpleRecyclerView() {
        with(fileListScreen) {
            recyclerView = R.id.fileListRecycler
            targetView = R.id.dateSimpleListItem
            targetCheckBox = R.id.checkboxSimpleListItem
        }
    }

    @Test
    fun b_verifyNoSnapshotsTaken_FMA_559() {
        setSimpleRecyclerView()
        mockUtils.clearSnapshotsOnX1()
        with(liveViewScreen) {
            openSnapshotList()

            fileListScreen.areNoFilesFound(R.string.no_images_found)
            fileListScreen.goBack()

            takeSnapshot()
            takeSnapshot()
            takeSnapshot()

            BaristaSleepInteractions.sleep(2000)

            openSnapshotList()

            fileListScreen.areFilesSortedByDate(
                extraSnapshotsList.items.subList(0, 3)
            )
        }
    }

    @Test
    fun a_verifyCheckboxWhenSnapshotsDontFit_FMA_560() {
        setSimpleRecyclerView()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSelectFilesToAssociate()
            areFilesSortedByDate(snapshotsList.items)

            selectCheckboxOnPosition(0)
            isCheckboxSelected(0)

            areCheckboxesUnselected(
                startPosition = 1,
                endPosition = snapshotsQuantity - 1
            )

            selectCheckboxOnPosition(0)
            selectCheckboxOnPosition(7)
            selectCheckboxOnPosition(14)

            isCheckboxSelected(14)
            isCheckboxSelected(7)
            isCheckboxUnselected(0)

            selectCheckboxOnPosition(7)
            selectCheckboxOnPosition(14)

            areCheckboxesUnselected(
                startPosition = 0,
                endPosition = snapshotsQuantity - 1
            )
        }
    }

    @Test
    fun verifyCheckboxFunctionality_FMA_561() {
        setSimpleRecyclerView()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            areFilesSortedByDate(snapshotsList.items)

            selectCheckboxOnPosition(0)
            isCheckboxSelected(0)

            selectCheckboxOnPosition(4)
            isCheckboxSelected(4)

            selectCheckboxOnPosition(7)
            isCheckboxSelected(7)

            selectCheckboxOnPosition(0)
            selectCheckboxOnPosition(4)
            selectCheckboxOnPosition(7)

            isCheckboxUnselected(0)
            isCheckboxUnselected(4)
            isCheckboxUnselected(7)
        }
    }

    @Test
    fun verifyScrollWhenSnapshotsDontFit_FMA_562() {
        setSimpleRecyclerView()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            scrollListToPosition(snapshotsQuantity - 1)
            scrollListToPosition(0)
            scrollListToPosition(snapshotsQuantity - 1)
            scrollListToPosition(0)
            areFilesSortedByDate(snapshotsList.items)
        }
    }

    @Test
    fun c_verifyUpdatingSnapshotsList_FMA_563() {
        setSimpleRecyclerView()
        mockUtils.clearSnapshotsOnX1()
        val takenSnapshots = extraSnapshotsList.items.subList(0, 3)

        with(liveViewScreen) {
            openSnapshotList()

            fileListScreen.goBack()

            isLiveViewDisplayed()

            takeSnapshot()
            takeSnapshot()
            takeSnapshot()

            BaristaSleepInteractions.sleep(2000)

            openSnapshotList()

            fileListScreen.areFilesSortedByDate(takenSnapshots)
        }
    }

    @Test
    fun verifyDisconnectionOpeningSnapsList_FMA_566() {
        setSimpleRecyclerView()
        mockUtils.disconnectCamera()

        liveViewScreen.openSnapshotList()
        liveViewScreen.isDisconnectionAlertDisplayed()
    }

    @Test
    fun verifyDisconnectionSwitchToVideosList_FMA_567() {
        setSimpleRecyclerView()
        liveViewScreen.openSnapshotList()
        fileListScreen.goBack()

        mockUtils.disconnectCamera()

        liveViewScreen.openVideoList()

        fileListScreen.isDisconnectionAlertDisplayed()
    }
}
