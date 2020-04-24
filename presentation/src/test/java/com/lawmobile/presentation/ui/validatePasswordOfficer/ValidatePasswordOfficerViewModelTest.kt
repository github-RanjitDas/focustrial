package com.lawmobile.presentation.ui.validatePasswordOfficer

import com.lawmobile.domain.entity.DomainUser
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.CameraHelper
import com.safefleet.mobile.commons.helpers.Result
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
class ValidatePasswordOfficerViewModelTest {

    //region mocks
    private val validatePasswordOfficerUseCase: ValidatePasswordOfficerUseCase = mockk()
    private val cameraHelper: CameraHelper = mockk()

    //endregion
    private val viewModel: ValidatePasswordOfficerViewModel by lazy {
        ValidatePasswordOfficerViewModel(validatePasswordOfficerUseCase, cameraHelper)
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
        viewModel.getUserInformation()
        coVerify { validatePasswordOfficerUseCase.getUserInformation() }
    }

    @Test
    fun testGetInformationUserSuccess() {
        coEvery { validatePasswordOfficerUseCase.getUserInformation() } returns Result.Success(
            DomainUser("1", "", "")
        )
        viewModel.getUserInformation()
        Assert.assertEquals(viewModel.domainUser.value, DomainUser("1", "", ""))
    }

    @Test
    fun testGetInformationUserError() {
        coEvery { validatePasswordOfficerUseCase.getUserInformation() } returns Result.Error(
            Exception("Error")
        )
        viewModel.getUserInformation()
        Assert.assertEquals(viewModel.errorDomainUser.value, "Error")
    }

    @Test
    fun testCreateSingletonCameraHelper() {
        mockkObject(CameraHelper)
        every { CameraHelper.setInstance(any()) } just Runs

        viewModel.createSingletonCameraHelper()
        verify { CameraHelper.setInstance(cameraHelper) }

    }
}