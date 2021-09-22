package com.lawmobile.presentation.ui.login.validateOfficerPassword

import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class ValidateOfficerPasswordViewModelTest {

    private val typeOfCameraUseCase: TypeOfCameraUseCase = mockk()

    private val passwordViewModel: ValidateOfficerPasswordViewModel by lazy {
        ValidateOfficerPasswordViewModel(typeOfCameraUseCase)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        clearMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testSetCameraTypeFlow() {
        coEvery { typeOfCameraUseCase.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        passwordViewModel.setCameraType()
        coVerify { typeOfCameraUseCase.getTypeOfCamera() }
    }

    @Test
    fun testSetCameraTypeFlowSuccessCameraTypeX2() {
        mockkObject(CameraInfo)
        every { CameraInfo.setCamera(any()) } just Runs
        coEvery { typeOfCameraUseCase.getTypeOfCamera() } returns Result.Success(CameraType.X2)
        passwordViewModel.setCameraType()
        coVerify { typeOfCameraUseCase.getTypeOfCamera() }
        verify { CameraInfo.setCamera(CameraType.X2) }
    }

    @Test
    fun testSetCameraTypeFlowSuccessCameraTypeX1() {
        mockkObject(CameraInfo)
        every { CameraInfo.setCamera(any()) } just Runs
        coEvery { typeOfCameraUseCase.getTypeOfCamera() } returns Result.Success(CameraType.X1)
        passwordViewModel.setCameraType()
        coVerify { typeOfCameraUseCase.getTypeOfCamera() }
        verify { CameraInfo.setCamera(CameraType.X1) }
    }

    @Test
    fun testSetCameraTypeError() {
        mockkObject(CameraInfo)
        coEvery { typeOfCameraUseCase.getTypeOfCamera() } returns Result.Error(Exception(""))
        passwordViewModel.setCameraType()
        Assert.assertEquals(CameraInfo.cameraType, CameraType.X1)
    }
}
