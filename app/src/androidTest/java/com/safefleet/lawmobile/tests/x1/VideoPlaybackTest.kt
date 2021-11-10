package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.VideoPlaybackScreen
import com.safefleet.lawmobile.testData.VideoPlaybackMetadata
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class VideoPlaybackTest : EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val videoPlaybackScreen = VideoPlaybackScreen()
        val defaultMetadata = VideoPlaybackMetadata.DEFAULT_VIDEO_METADATA.value
        val extraMetadata = VideoPlaybackMetadata.EXTRA_VIDEO_METADATA.value
    }

    @Before
    fun login() {
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
    fun verifyEventMandatory() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            clickOnSave()
            isEventMandatoryDisplayed()
            selectEvent(defaultMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()
            fileListScreen.isFileListDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-728
     */
    @Test
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
}
