package com.lawmobile.presentation.ui.login.x1

import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCase
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.ui.login.model.LoginState
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
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
internal class LoginX1ViewModelTest {

    private val typeOfCameraUseCase: TypeOfCameraUseCase = mockk()
    private val useCase: ValidatePasswordOfficerUseCase = mockk()
    private val dispatcher = TestCoroutineDispatcher()
    private val viewModel = LoginX1ViewModel(typeOfCameraUseCase, useCase, dispatcher)

    @Test
    fun setLoginState() {
        val state = LoginState.X1.StartPairing
        viewModel.setLoginState(state)
        Assert.assertEquals(state, viewModel.loginState.value)
    }

    @Test
    fun getOfficerPasswordFromCameraWhenNull() {
        Assert.assertEquals("", viewModel.getOfficerPasswordFromCamera())
    }

    @Test
    fun setOfficerPasswordFromCamera() {
        val password = "123"
        viewModel.setOfficerPasswordFromCamera(password)
        Assert.assertEquals(password, viewModel.getOfficerPasswordFromCamera())
    }

    @Test
    fun testSetCameraTypeFlowSuccessCameraTypeX2() {
        mockkObject(CameraInfo)
        every { CameraInfo.setCamera(any()) } just Runs
        coEvery { typeOfCameraUseCase.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        viewModel.setCameraType()
        coVerify { typeOfCameraUseCase.getTypeOfCamera() }
        verify { CameraInfo.setCamera(CameraType.X2) }
    }

    @Test
    fun testSetCameraTypeFlowSuccessCameraTypeX1() {
        mockkObject(CameraInfo)
        every { CameraInfo.setCamera(any()) } just Runs
        coEvery { typeOfCameraUseCase.getTypeOfCamera() } returns Result.Success(CameraType.X1)
        viewModel.setCameraType()
        coVerify { typeOfCameraUseCase.getTypeOfCamera() }
        verify { CameraInfo.setCamera(CameraType.X1) }
    }

    @Test
    fun testSetCameraTypeError() {
        mockkObject(CameraInfo)
        coEvery { typeOfCameraUseCase.getTypeOfCamera() } returns Result.Error(Exception(""))
        viewModel.setCameraType()
        Assert.assertEquals(CameraInfo.cameraType, CameraType.X1)
    }
}
