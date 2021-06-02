package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.SmokeTest
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.FilterDialogScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.VideoPlaybackScreen
import com.safefleet.lawmobile.testData.CameraFilesData
import com.safefleet.lawmobile.testData.VideoPlaybackMetadata
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class VideoListTest : EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {
    companion object {
        private val videoList = CameraFilesData.DEFAULT_VIDEO_LIST.value
        private val videosQuantity = videoList.items.size
        private val extraVideoList = CameraFilesData.EXTRA_VIDEO_LIST.value
        private val defaultMetadata = VideoPlaybackMetadata.DEFAULT_VIDEO_METADATA.value

        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val videoPlaybackScreen = VideoPlaybackScreen()
        private val filterDialogScreen = FilterDialogScreen()
    }

    @Before
    fun login() = LoginScreen().login()

    private fun setSimpleRecyclerView() {
        with(fileListScreen) {
            targetView = R.id.dateSimpleListItem
            targetCheckBox = R.id.checkboxSimpleListItem
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-575
     */
    @MediumTest
    @Test
    fun verifyNoVideosTaken() {
        setSimpleRecyclerView()
        mockUtils.clearVideosOnX1()

        with(liveViewScreen) {
            openVideoList()

            fileListScreen.areNoFilesFound(R.string.no_videos_found)
            fileListScreen.clickOnBack()

            startRecording()
            stopRecording()
            startRecording()
            stopRecording()
            startRecording()
            stopRecording()

            openVideoList()
        }

        fileListScreen.areFilesSortedByDate(
            extraVideoList.items.subList(0, 3)
        )
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1179
     */
    @Test
    fun verifyCheckboxWhenSnapshotsDontFit() {
        setSimpleRecyclerView()
        liveViewScreen.openVideoList()

        with(fileListScreen) {
            sleep(1200)
            clickOnSelectFilesToAssociate()
            areFilesSortedByDate(videoList.items)

            selectCheckboxOnPosition(0)
            isCheckboxSelected(0)

            areCheckboxesUnselected(
                startPosition = 1,
                endPosition = videosQuantity - 1
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
                endPosition = videosQuantity - 1
            )
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-561
     */
    @SmokeTest
    @Test
    fun verifyCheckboxFunctionality() {
        setSimpleRecyclerView()
        liveViewScreen.openVideoList()

        with(fileListScreen) {
            sleep(1200)
            clickOnSelectFilesToAssociate()
            areFilesSortedByDate(videoList.items)

            selectCheckboxOnPosition(0)
            isCheckboxSelected(0)

            areCheckboxesUnselected(
                startPosition = 1,
                endPosition = videosQuantity - 1
            )

            selectCheckboxOnPosition(0)
            selectCheckboxOnPosition(3)
            selectCheckboxOnPosition(5)

            isCheckboxUnselected(0)
            isCheckboxSelected(3)
            isCheckboxSelected(5)

            selectCheckboxOnPosition(3)
            selectCheckboxOnPosition(5)

            areCheckboxesUnselected(
                startPosition = 0,
                endPosition = videosQuantity - 1
            )
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-579
     */
    @Test
    fun verifyScrollWhenVideosDontFit() {
        setSimpleRecyclerView()
        liveViewScreen.openVideoList()

        with(fileListScreen) {
            scrollListToPosition(videosQuantity - 1)
            scrollListToPosition(0)
            scrollListToPosition(videosQuantity - 1)
            scrollListToPosition(0)
            areFilesSortedByDate(videoList.items)
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-578
     */
    @SmokeTest
    @Test
    fun verifyUpdatingVideosList() {
        mockUtils.clearVideosOnX1()
        setSimpleRecyclerView()
        val takenVideos = extraVideoList.items.subList(0, 3)

        with(liveViewScreen) {
            openVideoList()

            fileListScreen.isSelectVideosToAssociateDisplayed()
            fileListScreen.isVideosListScreenDisplayed()
            fileListScreen.clickOnBack()

            isLiveViewDisplayed()

            startRecording()
            stopRecording()
            startRecording()
            stopRecording()
            startRecording()
            stopRecording()

            openVideoList()
        }

        fileListScreen.areFilesSortedByDate(takenVideos)
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-580
     */
    @Test
    fun verifyDisconnectionOpeningVideoList() {
        mockUtils.disconnectCamera()

        liveViewScreen.openVideoList()
        liveViewScreen.isDisconnectionAlertDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-580
     */
    @Test
    fun verifyDisconnectionGoingBackToLive() {
        liveViewScreen.openVideoList()

        mockUtils.disconnectCamera()
        fileListScreen.clickOnBack()
        fileListScreen.isDisconnectionAlertDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1176
     */
    @SmokeTest
    @Test
    fun associateVideoToPartner() {
        setSimpleRecyclerView()
        liveViewScreen.openVideoList()

        with(fileListScreen) {
            sleep(1200)
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
     * Test case: https://safefleet.atlassian.net/browse/FMA-1283
     */
    @Test
    fun filterVideos() {
        liveViewScreen.openVideoList()

        with(fileListScreen) {
            matchItemsCount(15)
            isFilterButtonDisplayed()
            openFilterDialog()

            with(filterDialogScreen) {
                selectEvent("Default")
                selectStartDate(2020, 5, 20)
                clickOnOk()
                applyFilter()

                isFilterActive()
                isNoFilesFoundDisplayed()

                openFilterDialog()
                clearStartDate()
                selectEvent(R.string.no_event)
                applyFilter()

                isFilterActive()
                matchItemsCount(15)

                clickOnItemInPosition(1)
                videoPlaybackScreen.selectEvent(defaultMetadata)
                videoPlaybackScreen.clickOnSave()

                isFilterActive()
                matchItemsCount(14)
            }
        }
    }
}
