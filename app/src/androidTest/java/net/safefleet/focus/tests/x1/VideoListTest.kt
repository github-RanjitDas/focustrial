package net.safefleet.focus.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.lawmobile.body_cameras.entities.CameraCatalog
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions.waitUntil
import net.safefleet.focus.helpers.MockUtils.Companion.bodyCameraServiceMock
import net.safefleet.focus.helpers.SmokeTest
import net.safefleet.focus.screens.FileListScreen
import net.safefleet.focus.screens.FilterDialogScreen
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.screens.VideoPlaybackScreen
import net.safefleet.focus.testData.CameraFilesData
import net.safefleet.focus.testData.VideoPlaybackMetadata
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class VideoListTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        private val videoList = CameraFilesData.DEFAULT_VIDEO_LIST.value
        private val videosQuantity = videoList.items.size
        private val extraVideoList = CameraFilesData.EXTRA_VIDEO_LIST.value
        private val defaultMetadata = VideoPlaybackMetadata.DEFAULT_VIDEO_METADATA.value
        private val extraMetadata = VideoPlaybackMetadata.EXTRA_VIDEO_METADATA.value

        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val videoPlaybackScreen = VideoPlaybackScreen()
        private val filterDialogScreen = FilterDialogScreen()
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().login()
        FeatureSupportHelper.supportAssociateOfficerID = true
    }

    private fun setSimpleRecyclerView() {
        with(fileListScreen) {
            targetView = R.id.dateSimpleListItem
            targetCheckBox = R.id.checkboxSimpleListItem
        }
    }

    /**
     * Test cases:
     * https://safefleet.atlassian.net/browse/FMA-578
     * https://safefleet.atlassian.net/browse/FMA-723
     *
     */
    @SmokeTest
    @Test
    @AllowFlaky(attempts = 1)
    fun recordVideoAndUpdateMetadata() {
        setSimpleRecyclerView()
        mockUtils.clearVideosOnBodyCamera()

        with(liveViewScreen) {
            isLiveViewDisplayed()
            openVideoList()
            fileListScreen.areNoFilesFound(R.string.no_videos_found)
            fileListScreen.clickOnBack()
            startRecording()
            stopRecording()
            openVideoList()
        }

        fileListScreen.clickOnItemInPosition(0)

        defaultMetadata.metadata?.apply {
            event = CameraCatalog("2", "Disk Clean", "Event")
            ticketNumber = "TC001"
            dispatchNumber = "DP001"
            firstName = "John"
            lastName = "Copeland"
            location = "Miami"
        }

        with(videoPlaybackScreen) {
            selectEvent(defaultMetadata)
            sleep(1000)
            with(defaultMetadata.metadata) {
                updateField(R.id.ticket1Value, this?.ticketNumber)
                updateField(R.id.dispatch1Value, this?.dispatchNumber)
                updateField(R.id.firstNameValue, this?.firstName)
                updateField(R.id.lastNameValue, this?.lastName)
                updateField(R.id.locationValue, this?.location)
            }
            clickOnSave()
            isSavedSuccessDisplayed()

            bodyCameraServiceMock.setIsVideoUpdated(true)
            bodyCameraServiceMock.setUpdatedMetadata(defaultMetadata)

            fileListScreen.checkFileEvent(defaultMetadata.metadata?.event?.name)
            fileListScreen.clickOnItemInPosition(0)
            fileListScreen.checkFileEvent(defaultMetadata.metadata?.event?.name)

            with(defaultMetadata.metadata) {
                checkIfFieldIsUpdated(R.id.ticket1Value, this?.ticketNumber)
                checkIfFieldIsUpdated(R.id.dispatch1Value, this?.dispatchNumber)
                checkIfFieldIsUpdated(R.id.firstNameValue, this?.firstName)
                checkIfFieldIsUpdated(R.id.lastNameValue, this?.lastName)
                checkIfFieldIsUpdated(R.id.locationValue, this?.location)
            }
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-575
     */
    @MediumTest
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyNoVideosTaken() {
        setSimpleRecyclerView()
        mockUtils.clearVideosOnBodyCamera()

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
    @AllowFlaky(attempts = 1)
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
    @Test
    @AllowFlaky(attempts = 1)
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
    @AllowFlaky(attempts = 1)
    fun verifyScrollWhenVideosDontFit() {
        setSimpleRecyclerView()
        liveViewScreen.openVideoList()

        with(fileListScreen) {
            waitUntil { scrollListToPosition(videosQuantity - 1) }
            scrollListToPosition(0)
            scrollListToPosition(videosQuantity - 1)
            scrollListToPosition(0)
            areFilesSortedByDate(videoList.items)
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-578
     */
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyUpdatingVideosList() {
        mockUtils.clearVideosOnBodyCamera()
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
    @AllowFlaky(attempts = 1)
    fun verifyDisconnectionOpeningVideoList() {
        mockUtils.disconnectCamera()

        liveViewScreen.openVideoList()
        liveViewScreen.isDisconnectionAlertDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-580
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyDisconnectionGoingBackToLive() {
        liveViewScreen.openVideoList()

        mockUtils.disconnectCamera()
        fileListScreen.clickOnBack()
        fileListScreen.isDisconnectionAlertDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1176
     */
    @Test
    @AllowFlaky(attempts = 1)
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
    @SmokeTest
    @Test
    @AllowFlaky(attempts = 2)
    fun filterVideos() {
        liveViewScreen.openVideoList()

        with(fileListScreen) {
            matchItemsCount(15)
            isFilterButtonDisplayed()
            openFilterDialog()

            with(filterDialogScreen) {
                selectEvent("No event")
                selectStartDate(2021, 5, 20)
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
