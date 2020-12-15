package com.lawmobile.presentation.ui.login.validateOfficerPassword

import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.CameraHelper
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.*
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
    private val cameraHelper: CameraHelper = mockk()

    //endregion
    private val passwordViewModel: ValidateOfficerPasswordViewModel by lazy {
        ValidateOfficerPasswordViewModel(validatePasswordOfficerUseCase, cameraHelper)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
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
    fun testCreateSingletonCameraHelper() {
        mockkObject(CameraHelper)
        every { CameraHelper.setInstance(any()) } just Runs

        passwordViewModel.createSingletonCameraHelper()
        verify { CameraHelper.setInstance(cameraHelper) }
    }
}