package net.safefleet.focus.tests.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions.retry
import net.safefleet.focus.helpers.MockUtils.Companion.bodyCameraServiceMock
import net.safefleet.focus.helpers.RecyclerViewHelper
import net.safefleet.focus.screens.VideoPlaybackScreen
import net.safefleet.focus.screens.integration.FileListScreen
import net.safefleet.focus.screens.integration.LiveViewScreen
import net.safefleet.focus.screens.integration.LoginScreen
import net.safefleet.focus.testData.TestLoginData
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import net.safefleet.focus.tests.x1.LoginTest.Companion.loginScreen
import net.safefleet.focus.tests.x1.VideoPlaybackTest
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
class IntegrationTest : EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        const val OFFICER = "murbanob"
        val OFFICER_PASSWORD = TestLoginData.OFFICER_PASSWORD.value
        private val fileListScreen = FileListScreen()
        val liveViewScreen = LiveViewScreen()
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
    @AllowFlaky(attempts = 1)
    fun a_verifyAppLogin() {
        with(loginScreen) {
            sleep(1000)
            isPairingScreenDisplayed()

            clickOnGo()

            isPairingSuccessDisplayed()

            sleep(1000)
            isLoginScreenDisplayed()

            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            liveViewScreen.isLiveViewDisplayed()
        }
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun b_associateSnapshotWithOfficer() {
        LoginScreen().login(OFFICER_PASSWORD)

        setSimpleListViews()

        with(liveViewScreen) {
            takeSnapshot()
            takeSnapshotIntegrationTest()
            takeSnapshotIntegrationTest()
            openSnapshotList()
        }

        with(fileListScreen) {
            clickOnSimpleListButton()

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
    @AllowFlaky(attempts = 1)
    fun c_updateVideoMetadata() {
        LoginScreen().login(OFFICER_PASSWORD)

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

            bodyCameraServiceMock.setIsVideoUpdated(true)
            bodyCameraServiceMock.setUpdatedMetadata(VideoPlaybackTest.defaultMetadata)

            fileListScreen.clickOnItemInPosition(0)
            fileListScreen.checkFileEvent(VideoPlaybackTest.defaultMetadata.metadata?.event?.name)
            checkIfFieldsAreFilled()

            fillAllFields(VideoPlaybackTest.extraMetadata)
            clickOnSave()
            isSavedSuccessDisplayed()

            fileListScreen.checkFileEvent(VideoPlaybackTest.extraMetadata.metadata?.event?.name)
            bodyCameraServiceMock.setIsVideoUpdated(true)
            bodyCameraServiceMock.setUpdatedMetadata(VideoPlaybackTest.extraMetadata)

            fileListScreen.clickOnItemInPosition(0)
            fileListScreen.checkFileEvent(VideoPlaybackTest.extraMetadata.metadata?.event?.name)
            checkIfFieldsAreUpdated(VideoPlaybackTest.extraMetadata)
        }
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun d_verifyTakeASnapshot() {
        LoginScreen().login(OFFICER_PASSWORD)

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
    @AllowFlaky(attempts = 1)
    fun e_verifyTakeAVideo() {
        LoginScreen().login(OFFICER_PASSWORD)

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
}
