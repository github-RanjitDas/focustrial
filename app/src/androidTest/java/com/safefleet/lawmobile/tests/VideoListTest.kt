package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.testData.CameraFilesData
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
    fun verifyNoVideosTaken_FMA_559() {
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

    @Test
    fun verifyCheckboxWhenSnapshotsDontFit_FMA_560() {
        setSimpleRecyclerView()
        liveViewScreen.openVideoList()

        with(fileListScreen) {
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

    @Test
    fun verifyCheckboxFunctionality_FMA_561() {
        setSimpleRecyclerView()
        liveViewScreen.openVideoList()

        with(fileListScreen) {
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

    @Test
    fun verifyScrollWhenVideosDontFit_FMA_562() {
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

    @Test
    fun verifyUpdatingVideosList_FMA_563() {
        mockUtils.clearVideosOnX1()
        setSimpleRecyclerView()
        val takenVideos = extraVideoList.items.subList(0, 3)

        with(liveViewScreen) {
            openVideoList()

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

    @Test
    fun verifyDisconnectionOpeningVideoList_FMA_566() {
        mockUtils.disconnectCamera()

        liveViewScreen.openVideoList()
        liveViewScreen.isDisconnectionAlertDisplayed()
    }

    @Test
    fun verifyDisconnectionGoingBackToLive_FMA_567() {
        liveViewScreen.openVideoList()

        mockUtils.disconnectCamera()
        fileListScreen.clickOnBack()
        fileListScreen.isDisconnectionAlertDisplayed()
    }

    @Test
    fun associateVideoToPartner_FMA_1176() {
        setSimpleRecyclerView()
        liveViewScreen.openVideoList()

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
}