package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class OnboardingCardTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val loginScreen = LoginScreen()
    private val description1 = "Please, turn on the Body-Camera hotspot"
    private val description2 = "Please enter your officer ID or email address as provided by your administrator and tap on the Continue button."
    private val description3 = "If you are in this screen, please enter your email and password and tap in the Login button"
    private val description4 = "If you are in this screen, please enter your device password and tap in the Connect button."

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2908
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyOnboardingCardsAreCorrectlyDisplayed() {
        with(loginScreen) {
            isLogoDisplayed()
            clickOnOnboardingCards()
            isLogoNotDisplayed()

            isFirstCardDisplayed(description1)
            clickOnSkipButton()
            isLogoDisplayed()

            clickOnOnboardingCards()
            swipeCardToTheLeft()
            isSecondCardDisplayed(description2)
            swipeCardToTheRight()
            isFirstCardDisplayed(description1)
            swipeCardToTheLeft()
            isSecondCardDisplayed(description2)

            swipeCardToTheLeft()
            isThirdCardDisplayed(description3)
            swipeCardToTheLeft()
            isFourthCardDisplayed(description4)
            clickOnStartNowButton()
            isLogoDisplayed()
        }
    }
}
