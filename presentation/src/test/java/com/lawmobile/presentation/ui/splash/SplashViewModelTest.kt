package com.lawmobile.presentation.ui.splash

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.ui.onBoardingCards.OnBoardingCardsActivity
import com.lawmobile.presentation.utils.Constants
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class SplashViewModelTest {

    private val preferences: Preferences = mockk(relaxed = true)
    private val dataStore: DataStore<Preferences> = mockk(relaxed = true) {
        coEvery { data } returns flowOf(preferences)
    }
    private val splashViewModel = SplashViewModel(dataStore)

    @BeforeEach
    fun setup() {
        mockkObject(CameraInfo)
    }

    @Test
    fun getNextActivityOnBoardingCards() {
        every { preferences[Constants.CAMERA_TYPE] } returns CameraType.X2.name
        every { preferences[Constants.ON_BOARDING_DISPLAYED] } returns false
        splashViewModel.getNextActivity()
        Assert.assertEquals(
            OnBoardingCardsActivity::class.java,
            splashViewModel.nextActivityResult.value
        )
        verify { preferences[Constants.CAMERA_TYPE] }
        verify { preferences[Constants.ON_BOARDING_DISPLAYED] }
    }

    @Test
    fun getNextActivityLoginX2() {
        every { preferences[Constants.CAMERA_TYPE] } returns CameraType.X2.name
        every { preferences[Constants.ON_BOARDING_DISPLAYED] } returns true
        splashViewModel.getNextActivity()
        Assert.assertEquals(
            LoginX2Activity::class.java,
            splashViewModel.nextActivityResult.value
        )
        verify { preferences[Constants.CAMERA_TYPE] }
        verify { preferences[Constants.ON_BOARDING_DISPLAYED] }
    }
}
