package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.helpers.Alert.isExitAppDialogDisplayed
import com.safefleet.lawmobile.screens.BaseScreen
import com.safefleet.lawmobile.screens.BodyWornDiagnosisScreen
import com.safefleet.lawmobile.screens.FileListScreen
import com.safefleet.lawmobile.screens.HelpPageScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.MainMenuScreen
import com.safefleet.lawmobile.screens.NotificationViewScreen
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainMenuTest : EspressoBaseTest() {

    private val mainMenuScreen = MainMenuScreen()
    private val notificationViewScreen = NotificationViewScreen()
    private val liveViewScreen = LiveViewScreen()
    private val diagnosisViewScreen = BodyWornDiagnosisScreen()
    private val baseScreen = BaseScreen()
    private val loginScreen = LoginScreen()
    private val fileListScreen = FileListScreen()
    private val helpScreen = HelpPageScreen()

    @get:Rule
    var baristaRule = BaristaRule.create(LoginActivity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
        LoginScreen().login()
        mainMenuScreen.clickOnMainMenu()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1762
     */
    @Test
    fun verifyOpenMainMenu() {
        with(mainMenuScreen) {
            isViewSnapshotsDisplayed()
            isViewVideosDisplayed()
            isViewNotificationsDisplayed()
            isBodyWornDiagnosisDisplayed()
            isViewHelpDisplayed()
            isCloseMenuButtonDisplayed()
            isLogoutButtonDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1919
     */
    @Test
    fun verifyNavigationToViewSnapshots() {
        with(mainMenuScreen) {
            isViewSnapshotsDisplayed()

            clickOnViewSnapshots()

            fileListScreen.isSelectDisplayed()
            fileListScreen.isSnapshotsListScreenDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1920
     */
    @Test
    fun verifyNavigationToViewVideos() {
        with(mainMenuScreen) {
            isViewVideosDisplayed()

            clickOnViewVideos()

            fileListScreen.isSelectDisplayed()
            fileListScreen.isVideosListScreenDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1921
     */
    @Test
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
    fun verifyCloseMainMenuFromSwipeRight() {
        with(mainMenuScreen) {
            swipeRightMainMenu()

            isMainMenuNotDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1766
     */
    @Test
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
        loginScreen.isPairingScreenDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1923
     */
    @Test
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
