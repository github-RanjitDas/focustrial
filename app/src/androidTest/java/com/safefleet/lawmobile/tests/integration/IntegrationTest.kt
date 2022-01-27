package com.safefleet.lawmobile.tests.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.retry
import com.safefleet.lawmobile.helpers.RecyclerViewHelper
import com.safefleet.lawmobile.screens.VideoPlaybackScreen
import com.safefleet.lawmobile.screens.integration.FileListScreen
import com.safefleet.lawmobile.screens.integration.LiveViewScreen
import com.safefleet.lawmobile.screens.integration.LoginScreen
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.safefleet.lawmobile.tests.x1.LoginTest.Companion.loginScreen
import com.safefleet.lawmobile.tests.x1.VideoPlaybackTest
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@LargeTest
@Suppress
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class IntegrationTest : EspressoStartActivityBaseTest<LoginX1Activity>(LoginX1Activity::class.java) {

    companion object {
        const val OFFICER = "murbanob"
        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val videoPlaybackScreen = VideoPlaybackScreen()
    }

    private fun setSimpleListViews() {
        with(fileListScreen) {
            targetView = R.id.dateSimpleListItem
            targetCheckBox = R.id.checkboxSimpleListItem
        }
    }

    private fun setSimpleRecyclerView() {
        with(fileListScreen) {
            targetView = R.id.dateSimpleListItem
            targetCheckBox = R.id.checkboxSimpleListItem
        }
    }

    private fun retry() {
        with(fileListScreen) {
            val isRetryDisplayed = isRetryDisplayed()
            if (isRetryDisplayed) retry { clickOnRetry() }
            sleep(2000)
        }
    }

    @Test
    fun a_verifyAppLogin() {
        with(loginScreen) {
            isPairingScreenDisplayed()

            clickOnGo()

            retryLogin()

            isPairingSuccessDisplayed()

            sleep(1000)
            isLoginScreenDisplayed()

            typePassword(OFFICER)
            clickOnLogin()

            liveViewScreen.isLiveViewDisplayed()
        }
    }

    @Test
    fun d_verifyTakeASnapshot() {
        LoginScreen().login(OFFICER)

        liveViewScreen.openSnapshotList()

        with(fileListScreen) {
            clickOnSimpleListButton()
            reviewItemsCount(greaterThanOrEqualTo(0))
            clickOnBack()
        }

        val currentLength = RecyclerViewHelper.currentLength

        with(liveViewScreen) {
            takeSnapshot()
            openSnapshotList()
        }

        with(fileListScreen) {
            clickOnSimpleListButton()
            reviewItemsCount(`is`(currentLength + 1))
        }
    }

    @Test
    fun e_verifyTakeAVideo() {
        LoginScreen().login(OFFICER)

        liveViewScreen.openVideoList()

        with(fileListScreen) {
            reviewItemsCount(greaterThanOrEqualTo(0))
            clickOnBack()
        }

        val currentLength = RecyclerViewHelper.currentLength

        with(liveViewScreen) {
            startRecording()
            stopRecording()
            openVideoList()
        }

        with(fileListScreen) {
            reviewItemsCount(`is`(currentLength + 1))
        }
    }

    @Test
    fun b_associateSnapshotWithOfficer() {
        LoginScreen().login(OFFICER)

        setSimpleListViews()

        with(liveViewScreen) {
            takeSnapshot()
            takeSnapshotIntegrationTest()
            takeSnapshotIntegrationTest()
            openSnapshotList()
        }

        retry()

        with(fileListScreen) {
            clickOnSimpleListButton()
            clickOnSelectFilesToAssociate()

            selectCheckboxOnPosition(0)
            selectCheckboxOnPosition(2)
            clickOnAssociateWithAnOfficer()
            typeOfficerIdToAssociate(OFFICER)

            clickOnButtonAssignToOfficer()
            isAssociatePartnerSuccessMessageDisplayed()

            clickOnItemInPosition(0)
            isOfficerAssociatedDisplayed(OFFICER)

            clickOnBack()

            clickOnItemInPosition(2)
            isOfficerAssociatedDisplayed(OFFICER)
        }
    }

    @Test
    fun c_updateVideoMetadata() {
        LoginScreen().login(OFFICER)

        setSimpleRecyclerView()

        with(liveViewScreen) {
            startRecording()
            stopRecording()
            openVideoList()
        }

        retry()
        fileListScreen.clickOnItemInPosition(0)

        with(videoPlaybackScreen) {
            fillAllFields(VideoPlaybackTest.defaultMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()
            fileListScreen.checkFileEvent(VideoPlaybackTest.defaultMetadata.metadata?.event?.name)
            fileListScreen.clickOnItemInPosition(0)
            checkIfFieldsAreFilled()
            fillAllFields(VideoPlaybackTest.extraMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()
            fileListScreen.clickOnItemInPosition(0)
            fileListScreen.checkFileEvent(VideoPlaybackTest.extraMetadata.metadata?.event?.name)
            checkIfFieldsAreUpdated(VideoPlaybackTest.extraMetadata)
        }
    }
}
