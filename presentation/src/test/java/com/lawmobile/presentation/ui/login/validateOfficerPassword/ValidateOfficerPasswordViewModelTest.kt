package com.lawmobile.presentation.ui.login.validateOfficerPassword

import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCase
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.CameraHelper
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
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class ValidateOfficerPasswordViewModelTest {

    //region mocks
    private val validatePasswordOfficerUseCase: ValidatePasswordOfficerUseCase = mockk()
    private val typeOfCameraUseCase: TypeOfCameraUseCase = mockk()
    private val cameraHelper: CameraHelper = mockk()

    //endregion
    private val passwordViewModel: ValidateOfficerPasswordViewModel by lazy {
        ValidateOfficerPasswordViewModel(validatePasswordOfficerUseCase, typeOfCameraUseCase, cameraHelper)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        clearMocks()
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetInformationUserFlow() {
        coEvery { validatePasswordOfficerUseCase.getUserInformation() } returns Result.Success(
            DomainUser("1", "", "")
        )
        passwordViewModel.getUserInformation()
        coVerify { validatePasswordOfficerUseCase.getUserInformation() }
    }

    @Test
    fun testGetInformationUserSuccess() {
        val result = Result.Success(DomainUser("1", "", ""))
        coEvery { validatePasswordOfficerUseCase.getUserInformation() } returns result
        passwordViewModel.getUserInformation()
        Assert.assertEquals(passwordViewModel.domainUserLiveData.value, result)
    }

    @Test
    fun testGetInformationUserError() {
        coEvery { validatePasswordOfficerUseCase.getUserInformation() } returns Result.Error(
            Exception("Error")
        )
        passwordViewModel.getUserInformation()
        Assert.assertTrue(passwordViewModel.domainUserLiveData.value is Result.Error)
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

    @Test
    fun testCreateSingletonCameraHelper() {
        mockkObject(CameraHelper)
        every { CameraHelper.setInstance(any()) } just Runs

        passwordViewModel.createSingletonCameraHelper()
        verify { CameraHelper.setInstance(cameraHelper) }
    }
}
