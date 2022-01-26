package com.lawmobile.presentation.ui.login

import com.lawmobile.domain.entities.User
import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCamera
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.PairingViewModelTest
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LoginBaseViewModelTest {

    private val getUserFromCamera: GetUserFromCamera = mockk()
    private val dispatcher = TestCoroutineDispatcher()
    private val wifiHelper: WifiHelper = mockk {
        every { isEqualsValueWithSSID(PairingViewModelTest.DEFAULT_SSID) } returns true
        every { isEqualsValueWithSSID("X") } returns false
    }
    private val baseViewModel = LoginBaseViewModel(useCase, dispatcher)

    @Test
    fun setInstructionsOpenTrue() {
        baseViewModel.isInstructionsOpen = true
        Assert.assertTrue(baseViewModel.isInstructionsOpen)
    }

    @Test
    fun setInstructionsOpenFalse() {
        baseViewModel.isInstructionsOpen = false
        Assert.assertFalse(baseViewModel.isInstructionsOpen)
    }

    @Test
    fun testGetUserFromCameraFlow() {
        coEvery { getUserFromCamera() } returns Result.Success(
            User("1", "", "")
        )
        loginBaseViewModel.getUserFromCamera()
        coVerify { getUserFromCamera() }
    }

    @Test
    fun testGetUserFromCameraSuccess() {
        val result = Result.Success(User("1", "", ""))
        coEvery { getUserFromCamera() } returns result
        loginBaseViewModel.getUserFromCamera()
        Assert.assertEquals(loginBaseViewModel.userFromCameraResult.value, result)
    }

    @Test
    fun testGetUserFromCameraError() {
        coEvery { getUserFromCamera() } returns Result.Error(Exception("Error"))
        loginBaseViewModel.getUserFromCamera()
        Assert.assertTrue(loginBaseViewModel.userFromCameraResult.value is Result.Error)
    }

    @Test
    fun suggestWiFiNetwork() {
        every { wifiHelper.suggestWiFiNetwork(any(), any(), any()) } returns Unit
        loginBaseViewModel.suggestWiFiNetwork("", "") {}
        verify { wifiHelper.suggestWiFiNetwork(any(), any(), any()) }
    }}
