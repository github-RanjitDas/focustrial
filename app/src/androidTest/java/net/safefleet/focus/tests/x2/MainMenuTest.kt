package net.safefleet.focus.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.helpers.Alert.isExitAppDialogDisplayed
import net.safefleet.focus.helpers.SmokeTest
import net.safefleet.focus.screens.BaseScreen
import net.safefleet.focus.screens.BodyWornDiagnosisScreen
import net.safefleet.focus.screens.FileListScreen
import net.safefleet.focus.screens.HelpPageScreen
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.screens.MainMenuScreen
import net.safefleet.focus.screens.NotificationViewScreen
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainMenuTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val mainMenuScreen = MainMenuScreen()
    private val notificationViewScreen = NotificationViewScreen()
    private val liveViewScreen = LiveViewScreen()
    private val diagnosisViewScreen = BodyWornDiagnosisScreen()
    private val baseScreen = BaseScreen()
    private val loginScreen = LoginScreen()
    private val fileListScreen = FileListScreen()
    private val helpScreen = HelpPageScreen()

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().loginWithoutSSO()
        mainMenuScreen.clickOnMainMenu()
    }

    /**
     * Test cases:
     * https://safefleet.atlassian.net/browse/FMA-1921
     * https://safefleet.atlassian.net/browse/FMA-1919
     * https://safefleet.atlassian.net/browse/FMA-1796
     * https://safefleet.atlassian.net/browse/FMA-1762
     */
    @SmokeTest
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyNavigationMainMenu() {
        with(mainMenuScreen) {
            isDashboardDisplayed()
            isViewSnapshotsDisplayed()
            isViewVideosDisplayed()
            isViewNotificationsDisplayed()
            isBodyWornDiagnosisDisplayed()
            isViewHelpDisplayed()
            isCloseMenuButtonDisplayed()
            isLogoutButtonDisplayed()

            clickOnNotifications()
            notificationViewScreen.isNotificationViewDisplayed()
            baseScreen.clickOnBack()
            liveViewScreen.isLiveViewTextDisplayed()
            clickOnMainMenu()
            clickOnViewSnapshots()
        }

        fileListScreen.isSnapshotsTitleDisplayed()

        with(notificationViewScreen) {
            clickOnBellButton()
            isNotificationTitleDisplayed()
        }

        baseScreen.clickOnBack()
        fileListScreen.isSnapshotsTitleDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1919
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyNavigationToViewSnapshots() {
        FeatureSupportHelper.supportAssociateOfficerID = true

        with(mainMenuScreen) {
            isViewSnapshotsDisplayed()
            clickOnViewSnapshots()
            fileListScreen.isSnapshotsListScreenDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1920
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyNavigationToViewVideos() {
        with(mainMenuScreen) {
            isViewVideosDisplayed()
            clickOnViewVideos()
            fileListScreen.isVideosListScreenDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1921
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyNavigationToViewNotifications() {
        with(mainMenuScreen) {
            isViewNotificationsDisplayed()
            clickOnNotifications()
            notificationViewScreen.isNotificationViewDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1922
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyNavigationToViewDiagnosis() {
        with(mainMenuScreen) {
            isBodyWornDiagnosisDisplayed()
            clickOnViewDiagnose()
            diagnosisViewScreen.isStartButtonDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1763
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyNavigationToViewHelp() {
        with(mainMenuScreen) {
            isViewHelpDisplayed()
            clickOnViewHelp()
            helpScreen.isUserGuideDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1765
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyCloseMainMenuFromCloseButton() {
        with(mainMenuScreen) {
            isCloseMenuButtonDisplayed()
            clickOnCloseMenu()
            isMainMenuNotDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1768
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyCloseMainMenuFromSwipeRight() {
        with(mainMenuScreen) {
            swipeRightMainMenu()
            isMainMenuNotDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1766
     */
    @SmokeTest
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyLogoutAndAcceptFromMainMenu() {
        with(mainMenuScreen) {
            isLogoutButtonDisplayed()
            clickOnLogout()
        }
        with(baseScreen) {
            isExitAppDialogDisplayed()
            isAcceptOptionDisplayed()
            clickOnAccept()
        }
        loginScreen.isOfficerIdLabelDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1923
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyLogoutAndCancelFromMainMenu() {
        with(mainMenuScreen) {
            isLogoutButtonDisplayed()
            clickOnLogout()

            with(baseScreen) {
                isExitAppDialogDisplayed()
                isCancelOptionDisplayed()
                clickOnCancel()
            }

            isMainMenuNotDisplayed()
            liveViewScreen.isLiveViewDisplayed()
        }
    }
}
