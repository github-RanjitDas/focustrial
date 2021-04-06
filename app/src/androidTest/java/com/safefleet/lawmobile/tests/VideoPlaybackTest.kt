package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.VideoPlaybackScreen
import com.safefleet.lawmobile.testData.VideoPlaybackMetadata
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
        private val defaultMetadata = VideoPlaybackMetadata.DEFAULT_VIDEO_METADATA.value
        private val extraMetadata = VideoPlaybackMetadata.EXTRA_VIDEO_METADATA.value
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

    @Test
    fun verifySaveVideoMetadataSuccess_FMA_722() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            fillAllFields(defaultMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()
        }
        fileListScreen.checkFileEvent(defaultMetadata.metadata?.event?.name)
    }

    @LargeTest
    @Test
    fun verifyEventMandatory_FMA_727() {
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

    @Test
    fun verifySaveWhenDisconnectionOnPlayback_FMA_728() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            selectEvent(defaultMetadata)
            mockUtils.disconnectCamera()
            clickOnSave()
            isDisconnectionAlertDisplayed()
        }
    }

    @Test
    fun verifyCancelWhenDisconnectionOnPlayback_FMA_729() {
        liveViewScreen.openVideoList()
        fileListScreen.clickOnItemInPosition(3)
        with(videoPlaybackScreen) {
            mockUtils.disconnectCamera()
            clickOnBack()
            isDisconnectionAlertDisplayed()
        }
    }

    @LargeTest
    @Test
    fun verifySaveMetadataOnPlaybackWhenRecording_FMA_730() {
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

    @LargeTest
    @Test
    fun verifyCancelVideoMetadataOnPlayback_FMA_725() {
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

    @LargeTest
    @Test
    fun updateVideoMetadataOnPlayback_FMA_723() {
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
