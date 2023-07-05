package net.safefleet.focus.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.R
import net.safefleet.focus.screens.FileListScreen
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.screens.VideoPlaybackScreen
import net.safefleet.focus.testData.VideoPlaybackMetadata
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class VideoPlaybackTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val videoPlaybackScreen = VideoPlaybackScreen()
        val defaultMetadata = VideoPlaybackMetadata.DEFAULT_VIDEO_METADATA.value
        val extraMetadata = VideoPlaybackMetadata.EXTRA_VIDEO_METADATA.value
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().login()
        setSimpleRecyclerView()
    }

    private fun setSimpleRecyclerView() {
        with(fileListScreen) {
            targetView = R.id.dateSimpleListItem
            targetCheckBox = R.id.checkboxSimpleListItem
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-722
     */
    @Test
    @AllowFlaky(attempts = 2)
    fun verifySaveVideoMetadataSuccess() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            fillAllFields(defaultMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()
        }
        fileListScreen.checkFileEvent(defaultMetadata.metadata?.event?.name)
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-727
     */
    @LargeTest
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyEventMandatory() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            clickOnSave()
            isEventMandatoryDisplayed()
            selectEvent(defaultMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()
        }
        fileListScreen.isFileListDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-728
     */
    @Test
    @AllowFlaky(attempts = 2)
    fun verifySaveWhenDisconnectionOnPlayback() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            selectEvent(defaultMetadata)
            mockUtils.disconnectCamera()
            clickOnSave()
            isDisconnectionAlertDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-729
     */
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyCancelWhenDisconnectionOnPlayback() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            mockUtils.disconnectCamera()
            clickOnBack()
            isDisconnectionAlertDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-730
     */
    @LargeTest
    @Test
    @AllowFlaky(attempts = 2)
    fun verifySaveMetadataOnPlaybackWhenRecording() {
        with(liveViewScreen) {
            startRecording()
            isRecordingInProgress()
            openVideoList()
            fileListScreen.clickOnItemInPosition(3)
            with(videoPlaybackScreen) {
                selectEvent(defaultMetadata)
                typePartnerId(defaultMetadata)
                clickOnSave()
                isSavedSuccessDisplayed()
            }
            fileListScreen.clickOnBack()
            stopRecording()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-725
     */
    @LargeTest
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyCancelVideoMetadataOnPlayback() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            fillAllFields(defaultMetadata)
            clickOnBack()
            isMetadataChangesAlertDisplayed()
            clickOnCancel()
            checkIfFieldsAreFilled()
            clickOnBack()
            clickOnAccept()
        }
        fileListScreen.isFileListDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-723
     */
    @LargeTest
    @Test
    @AllowFlaky(attempts = 3)
    fun updateVideoMetadataOnPlayback() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            fillAllFields(defaultMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()
            fileListScreen.checkFileEvent(defaultMetadata.metadata?.event?.name)
            fileListScreen.clickOnItemInPosition(3)
            checkIfFieldsAreFilled()
            fillAllFields(extraMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()
            fileListScreen.checkFileEvent(extraMetadata.metadata?.event?.name)
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-3200
     */
    @LargeTest
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyVideoMetadataWhenFullScreenWasEnabledOnPlayback() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(2)
        with(videoPlaybackScreen) {
            fillAllFields(extraMetadata)

            clickOnFullScreen()
            clickOnFullScreen()

            checkIfDropdownsAreUpdated(extraMetadata)
            checkIfFieldsAreUpdated(extraMetadata)
        }
    }
}
