package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.SmokeTest
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.FilterDialogScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SnapshotListTest : EspressoStartActivityBaseTest<LoginX1Activity>(LoginX1Activity::class.java) {
    companion object {
        const val OFFICER = "murbanob"
        private val snapshotsList = CameraFilesData.DEFAULT_SNAPSHOT_LIST.value
        private val snapshotsQuantity = snapshotsList.items.size
        private val extraSnapshotsList = CameraFilesData.EXTRA_SNAPSHOT_LIST.value

        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val filterDialogScreen = FilterDialogScreen()
    }

    @Before
    fun setup() {
        LoginScreen().login()
        FeatureSupportHelper.supportAssociateOfficerID = true
    }

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

    /**
     * Test cases:
     * https://safefleet.atlassian.net/browse/FMA-1176
     * https://safefleet.atlassian.net/browse/FMA-563
     */
    @SmokeTest
    @Test
    fun takeSnapshotAndAssociateOfficer() {
        setSimpleListViews()
        mockUtils.clearSnapshotsOnX1()

        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            areNoFilesFound(R.string.no_images_found)
            clickOnBack()
        }

        with(liveViewScreen) {
            takeSnapshot()
            takeSnapshot()
            takeSnapshot()

            openSnapshotList()
        }

        with(fileListScreen) {
            clickOnSimpleListButton()
            clickOnSelectFilesToAssociate()
            selectCheckboxOnPosition(0)
            selectCheckboxOnPosition(1)
            clickOnAssociateWithAnOfficer()
            typeOfficerIdToAssociate(OFFICER)
            clickOnButtonAssignToOfficer()

            sleep(1000)
            clickOnItemInPosition(0)
            isOfficerAssociatedDisplayed(OFFICER)
            clickOnBack()
            clickOnItemInPosition(1)
            isOfficerAssociatedDisplayed(OFFICER)
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1091
     */
    @SmokeTest
    @Test
    fun sortSnapshots() {
        setSimpleListViews()
        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSimpleListButton()

            fileListScreen.areFilesSortedByDate(
                snapshotsList.items
            )

            clickOnDateAndTimeTitle()

            fileListScreen.areFilesSortedByAscendingDate((snapshotsList.items))
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-559
     */
    @Test
    fun verifyNoSnapshotsTakenSimpleList() {
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

            openSnapshotList()
            fileListScreen.clickOnSimpleListButton()

            fileListScreen.areFilesSortedByDate(
                extraSnapshotsList.items.subList(0, 3)
            )
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-559
     */
    @Test
    fun verifyNoSnapshotsTakenThumbnailList() {
        setThumbnailListViews()
        mockUtils.clearSnapshotsOnX1()
        with(liveViewScreen) {
            openSnapshotList()

            fileListScreen.areNoFilesFound(R.string.no_images_found)
            fileListScreen.clickOnBack()

            takeSnapshot()
            takeSnapshot()
            takeSnapshot()

            openSnapshotList()

            fileListScreen.areFilesSortedByDate(
                extraSnapshotsList.items.subList(0, 3)
            )
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-560
     */
    @Test
    fun verifySimpleCheckboxWhenSnapshotsDontFit() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-560
     */
    @Test
    fun verifyThumbnailCheckboxWhenSnapshotsDontFit() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-561
     */
    @Test
    fun verifySimpleCheckboxFunctionality() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-561
     */
    @Test
    fun verifyThumbnailCheckboxFunctionality() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-562
     */
    @Test
    fun verifyScrollWhenSimpleSnapshotsDontFit() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-562
     */
    @Test
    fun verifyScrollWhenThumbnailSnapshotsDontFit() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-563
     */
    @Test
    fun verifyUpdatingSimpleSnapshotsList() {
        setSimpleListViews()
        mockUtils.clearSnapshotsOnX1()
        val takenSnapshots = extraSnapshotsList.items.subList(0, 3)

        with(liveViewScreen) {
            openSnapshotList()
            fileListScreen.isSelectSnapshotsToAssociateDisplayed()
            fileListScreen.isSnapshotsListScreenDisplayed()
            fileListScreen.clickOnSimpleListButton()

            fileListScreen.clickOnBack()

            isLiveViewDisplayed()

            takeSnapshot()
            takeSnapshot()
            takeSnapshot()

            openSnapshotList()
            fileListScreen.clickOnSimpleListButton()

            fileListScreen.areFilesSortedByDate(takenSnapshots)
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-563
     */
    @Test
    fun verifyUpdatingThumbnailSnapshotsList() {
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

            openSnapshotList()

            fileListScreen.areFilesSortedByDate(takenSnapshots)
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-566
     */
    @Test
    fun verifyDisconnectionOpeningSnapsList() {
        setSimpleListViews()
        mockUtils.disconnectCamera()

        liveViewScreen.openSnapshotList()

        liveViewScreen.isDisconnectionAlertDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-567
     */
    @Test
    fun verifyDisconnectionSwitchToVideosList() {
        setSimpleListViews()
        liveViewScreen.openSnapshotList()
        fileListScreen.clickOnBack()

        mockUtils.disconnectCamera()

        liveViewScreen.openVideoList()

        fileListScreen.isDisconnectionAlertDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1176
     */
    @Test
    fun associateSimpleSnapshotToPartner() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1176
     */
    @Test
    fun associateThumbnailSnapshotToPartner() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1271
     */
    @Test
    fun filterThumbnailSnapshots() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1271
     */
    @Test
    fun filterSimpleSnapshots() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1274
     */
    @Test
    fun filterThumbnailSnapshotsNoMatch() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1274
     */
    @Test
    fun filterSimpleSnapshotsNoMatch() {
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

    /**
     * Test case:  https://safefleet.atlassian.net/browse/FMA-1281
     */
    @Test
    fun filterSnapshotsPersistence() {
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
