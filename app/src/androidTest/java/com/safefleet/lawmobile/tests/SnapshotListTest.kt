package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.FilterDialogScreen
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
class SnapshotListTest : EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {
    companion object {
        private val snapshotsList = CameraFilesData.DEFAULT_SNAPSHOT_LIST.value
        private val snapshotsQuantity = snapshotsList.items.size

        private val extraSnapshotsList = CameraFilesData.EXTRA_SNAPSHOT_LIST.value

        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val filterDialogScreen = FilterDialogScreen()
    }

    @Before
    fun login() = LoginScreen().login()

    private fun setSimpleListViews() {
        with(fileListScreen) {
            targetView = R.id.dateSimpleListItem
            targetCheckBox = R.id.checkboxSimpleListItem
        }
    }

    private fun setThumbnailListViews() {
        with(fileListScreen) {
            targetView = R.id.dateImageListItem
            targetCheckBox = R.id.checkboxImageListItem
        }
    }

    @Test
    fun b_verifyNoSnapshotsTakenSimpleList_FMA_559() {
        setSimpleListViews()
        mockUtils.clearSnapshotsOnX1()
        with(liveViewScreen) {
            openSnapshotList()
            fileListScreen.clickOnSimpleListButton()

            fileListScreen.areNoFilesFound(R.string.no_images_found)
            fileListScreen.clickOnBack()

            takeSnapshot()
            takeSnapshot()
            takeSnapshot()
            EspressoIdlingResource.increment()

            openSnapshotList()
            fileListScreen.clickOnSimpleListButton()

            fileListScreen.areFilesSortedByDate(
                extraSnapshotsList.items.subList(0, 3)
            )
        }
    }

    @Test
    fun b_verifyNoSnapshotsTakenThumbnailList_FMA_559() {
        setThumbnailListViews()
        mockUtils.clearSnapshotsOnX1()
        with(liveViewScreen) {
            openSnapshotList()

            fileListScreen.areNoFilesFound(R.string.no_images_found)
            fileListScreen.clickOnBack()

            takeSnapshot()
            takeSnapshot()
            takeSnapshot()
            EspressoIdlingResource.increment()

            openSnapshotList()

            fileListScreen.areFilesSortedByDate(
                extraSnapshotsList.items.subList(0, 3)
            )
        }
    }

    @Test
    fun a_verifySimpleCheckboxWhenSnapshotsDontFit_FMA_560() {
        setSimpleListViews()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSimpleListButton()
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
    fun a_verifyThumbnailCheckboxWhenSnapshotsDontFit_FMA_560() {
        setThumbnailListViews()
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
    fun verifySimpleCheckboxFunctionality_FMA_561() {
        setSimpleListViews()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSimpleListButton()
            areFilesSortedByDate(snapshotsList.items)

            clickOnSelectFilesToAssociate()

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
    fun verifyThumbnailCheckboxFunctionality_FMA_561() {
        setThumbnailListViews()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            areFilesSortedByDate(snapshotsList.items)

            clickOnSelectFilesToAssociate()

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
    fun verifyScrollWhenSimpleSnapshotsDontFit_FMA_562() {
        setSimpleListViews()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSimpleListButton()
            scrollListToPosition(snapshotsQuantity - 1)
            scrollListToPosition(0)
            scrollListToPosition(snapshotsQuantity - 1)
            scrollListToPosition(0)
            areFilesSortedByDate(snapshotsList.items)
        }
    }

    @Test
    fun verifyScrollWhenThumbnailSnapshotsDontFit_FMA_562() {
        setThumbnailListViews()
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
    fun c_verifyUpdatingSimpleSnapshotsList_FMA_563() {
        setSimpleListViews()
        mockUtils.clearSnapshotsOnX1()
        val takenSnapshots = extraSnapshotsList.items.subList(0, 3)

        with(liveViewScreen) {
            openSnapshotList()
            fileListScreen.clickOnSimpleListButton()

            fileListScreen.clickOnBack()

            isLiveViewDisplayed()

            takeSnapshot()
            takeSnapshot()
            takeSnapshot()

            BaristaSleepInteractions.sleep(2000)

            openSnapshotList()
            fileListScreen.clickOnSimpleListButton()

            BaristaSleepInteractions.sleep(2000)

            fileListScreen.areFilesSortedByDate(takenSnapshots)
        }
    }

    @Test
    fun c_verifyUpdatingThumbnailSnapshotsList_FMA_563() {
        setThumbnailListViews()
        mockUtils.clearSnapshotsOnX1()
        val takenSnapshots = extraSnapshotsList.items.subList(0, 3)

        with(liveViewScreen) {
            openSnapshotList()

            fileListScreen.clickOnBack()

            isLiveViewDisplayed()

            takeSnapshot()
            takeSnapshot()
            takeSnapshot()
            EspressoIdlingResource.increment()

            openSnapshotList()

            fileListScreen.areFilesSortedByDate(takenSnapshots)
        }
    }

    @Test
    fun verifyDisconnectionOpeningSnapsList_FMA_566() {
        setSimpleListViews()
        mockUtils.disconnectCamera()

        liveViewScreen.openSnapshotList()
        liveViewScreen.isDisconnectionAlertDisplayed()
    }

    @Test
    fun verifyDisconnectionSwitchToVideosList_FMA_567() {
        setSimpleListViews()
        liveViewScreen.openSnapshotList()
        fileListScreen.clickOnBack()

        mockUtils.disconnectCamera()

        liveViewScreen.openVideoList()

        fileListScreen.isDisconnectionAlertDisplayed()
    }

    @Test
    fun associateSimpleSnapshotToPartner_FMA_1176() {
        setSimpleListViews()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSimpleListButton()
            clickOnSelectFilesToAssociate()
            selectCheckboxOnPosition(1)
            selectCheckboxOnPosition(3)
            selectCheckboxOnPosition(5)
            clickOnAssociateWithAnOfficer()
            typeOfficerIdToAssociate("murbanob")
            clickOnButtonAssignToOfficer()
        }
    }

    @Test
    fun associateThumbnailSnapshotToPartner_FMA_1176() {
        setThumbnailListViews()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSelectFilesToAssociate()
            selectCheckboxOnPosition(1)
            selectCheckboxOnPosition(3)
            selectCheckboxOnPosition(5)
            clickOnAssociateWithAnOfficer()
            typeOfficerIdToAssociate("murbanob")
            clickOnButtonAssignToOfficer()
        }
    }

    @Test
    fun filterThumbnailSnapshots_FMA_1271() {
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {

            matchItemsCount(15)
            isFilterButtonDisplayed()
            openFilterDialog()

            with(filterDialogScreen) {
                selectStartDate(2020, 6, 1)
                clickOnOk()
                applyFilter()
                isFilterActive()
                matchItemsCount(6)

                openFilterDialog()

                selectEndDate(2020, 6, 2)
                clickOnOk()
                applyFilter()

                isFilterActive()
                matchItemsCount(6)

                clickOnRemoveTag()
                matchItemsCount(15)

                openFilterDialog()

                selectStartDate(2020, 5, 28)
                clickOnOk()
                applyFilter()
                isFilterActive()
                matchItemsCount(8)
            }
        }
    }

    @Test
    fun filterSimpleSnapshots_FMA_1271() {
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSimpleListButton()
            matchItemsCount(15)
            isFilterButtonDisplayed()
            openFilterDialog()

            with(filterDialogScreen) {
                selectStartDate(2020, 6, 1)
                clickOnOk()
                applyFilter()
                isFilterActive()
                matchItemsCount(6)

                openFilterDialog()

                selectEndDate(2020, 6, 2)
                clickOnOk()
                applyFilter()

                isFilterActive()
                matchItemsCount(6)

                clickOnRemoveTag()
                matchItemsCount(15)

                openFilterDialog()

                selectStartDate(2020, 5, 28)
                clickOnOk()
                applyFilter()
                isFilterActive()
                matchItemsCount(8)
            }
        }
    }

    @Test
    fun filterThumbnailSnapshotsNoMatch_FMA_1274() {
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {

            matchItemsCount(15)
            isFilterButtonDisplayed()
            openFilterDialog()

            with(filterDialogScreen) {
                selectStartDate()
                clickOnOk()
                clickOnOk()

                selectEndDate(2020, 5, 22)
                clickOnOk()

                applyFilter()

                isFilterActive()
                isNoFilesFoundDisplayed()

                openFilterDialog()
                clearStartDate()
                applyFilter()
                matchItemsCount(5)
            }
        }
    }

    @Test
    fun filterSimpleSnapshotsNoMatch_FMA_1274() {
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSimpleListButton()
            matchItemsCount(15)
            isFilterButtonDisplayed()
            openFilterDialog()

            with(filterDialogScreen) {
                selectStartDate()
                clickOnOk()
                clickOnOk()

                selectEndDate(2020, 5, 22)
                clickOnOk()

                applyFilter()

                isFilterActive()
                isNoFilesFoundDisplayed()

                openFilterDialog()
                clearStartDate()
                applyFilter()
                matchItemsCount(5)
            }
        }
    }

    @Test
    fun filterSnapshotsPersistence_FMA_1281() {
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            matchItemsCount(15)
            isFilterButtonDisplayed()
            openFilterDialog()

            with(filterDialogScreen) {
                selectEndDate(2020, 5, 22)
                clickOnOk()
                applyFilter()

                isFilterActive()
                matchItemsCount(5)

                clickOnSimpleListButton()

                isFilterActive()
                matchItemsCount(5)

                clickOnItemInPosition(1)
                clickOnBack()

                isFilterActive()
                matchItemsCount(5)
                clickOnBack()

                liveViewScreen.openSnapshotList()
                isFilterNotActive()
            }
        }
    }
}
