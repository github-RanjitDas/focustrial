package com.lawmobile.presentation.ui.login

import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validateOfficerPassword.ValidateOfficerPasswordUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.AuthStateManagerFactory
import com.safefleet.mobile.authentication.AuthStateManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import net.openid.appauth.AuthorizationRequest
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LoginActivityViewModelTest {

    private val useCase: ValidateOfficerPasswordUseCase = mockk()
    private val dispatcher = TestCoroutineDispatcher()
    private val authStateManager: AuthStateManager = mockk()
    private val authStateManagerFactory: AuthStateManagerFactory = mockk {
        every { create() } returns authStateManager
    }
    private val loginActivityViewModel =
        LoginActivityViewModel(useCase, authStateManagerFactory, dispatcher)

    @BeforeEach
    fun setUp() {
        clearMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetInformationUserFlow() {
        coEvery { useCase.getUserInformation() } returns Result.Success(
            DomainUser("1", "", "")
        )
        loginActivityViewModel.getUserInformation()
        coVerify { useCase.getUserInformation() }
    }

    @Test
    fun testGetInformationUserSuccess() {
        val result = Result.Success(DomainUser("1", "", ""))
        coEvery { useCase.getUserInformation() } returns result
        loginActivityViewModel.getUserInformation()
        Assert.assertEquals(loginActivityViewModel.userInformationResult.value, result)
    }

    @Test
    fun testGetInformationUserError() {
        coEvery { useCase.getUserInformation() } returns Result.Error(Exception("Error"))
        loginActivityViewModel.getUserInformation()
        Assert.assertTrue(loginActivityViewModel.userInformationResult.value is Result.Error)
    }

    @Test
    fun isUserAuthorizedTrue() {
        every { authStateManager.isCurrentAuthStateAuthorized() } returns true
        Assert.assertTrue(loginActivityViewModel.isUserAuthorized())
        verify { authStateManager.isCurrentAuthStateAuthorized() }
    }

    @Test
    fun isUserAuthorizedFalse() {
        every { authStateManager.isCurrentAuthStateAuthorized() } returns false
        Assert.assertFalse(loginActivityViewModel.isUserAuthorized())
        verify { authStateManager.isCurrentAuthStateAuthorized() }
    }

    @Test
    fun authenticateToGetTokenFlow() {
        every { authStateManager.exchangeAuthorizationCode(any(), any(), any()) } returns Unit
        loginActivityViewModel.authenticateToGetToken(mockk(), mockk())
        every { authStateManager.exchangeAuthorizationCode(any(), any(), any()) }
    }

    @Test
    fun getAuthRequestSuccess() {
        val authRequestResult = Result.Success(mockk<AuthorizationRequest>())
        every { authStateManager.createRequestToAuth() } returns authRequestResult
        loginActivityViewModel.getAuthRequest()
        Assert.assertEquals(authRequestResult, loginActivityViewModel.authRequestResult.value)
        verify { authStateManager.createRequestToAuth() }
    }

    @Test
    fun getAuthRequestError() {
        val authRequestResult = Result.Error(Exception())
        every { authStateManager.createRequestToAuth() } returns authRequestResult
        loginActivityViewModel.getAuthRequest()
        Assert.assertEquals(authRequestResult, loginActivityViewModel.authRequestResult.value)
        verify { authStateManager.createRequestToAuth() }
    }
}
