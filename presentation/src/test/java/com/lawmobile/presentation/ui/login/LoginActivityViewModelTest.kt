package com.lawmobile.presentation.ui.login

import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.usecase.LoginUseCases
import com.lawmobile.domain.utils.PreferencesManager
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.authentication.AuthStateManagerFactory
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
import kotlinx.coroutines.test.runBlockingTest
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

    private val useCases: LoginUseCases = mockk()
    private val dispatcher = TestCoroutineDispatcher()
    private val authStateManager: AuthStateManager = mockk()
    private val authStateManagerFactory: AuthStateManagerFactory = mockk {
        every { create(any()) } returns authStateManager
    }
    private val preferencesManager: PreferencesManager = mockk()
    private val loginActivityViewModel =
        LoginActivityViewModel(useCases, authStateManagerFactory, preferencesManager, dispatcher)

    @BeforeEach
    fun setUp() {
        clearMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetInformationUserFlow() {
        coEvery { useCases.getUserFromCamera() } returns Result.Success(
            User("1", "", "")
        )
        loginActivityViewModel.getUserFromCamera()
        coVerify { useCases.getUserFromCamera() }
    }

    @Test
    fun testGetInformationUserSuccess() {
        val result = Result.Success(User("1", "", ""))
        coEvery { useCases.getUserFromCamera() } returns result
        loginActivityViewModel.getUserFromCamera()
        Assert.assertEquals(loginActivityViewModel.userFromCameraResult.value, result)
    }

    @Test
    fun testGetInformationUserError() {
        coEvery { useCases.getUserFromCamera() } returns Result.Error(Exception("Error"))
        loginActivityViewModel.getUserFromCamera()
        Assert.assertTrue(loginActivityViewModel.userFromCameraResult.value is Result.Error)
    }

    @Test
    fun isUserAuthorizedTrue() {
        every { authStateManager.isCurrentAuthStateAuthorized() } returns true
        loginActivityViewModel.getAuthorizationRequest(mockk(relaxed = true))
        Assert.assertTrue(loginActivityViewModel.isUserAuthorized())
        verify { authStateManager.isCurrentAuthStateAuthorized() }
    }

    @Test
    fun isUserAuthorizedFalse() {
        every { authStateManager.isCurrentAuthStateAuthorized() } returns false
        loginActivityViewModel.getAuthorizationRequest(mockk(relaxed = true))
        Assert.assertFalse(loginActivityViewModel.isUserAuthorized())
        verify { authStateManager.isCurrentAuthStateAuthorized() }
    }

    @Test
    fun authenticateToGetTokenFlow() {
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        loginActivityViewModel.getAuthorizationRequest(authorizationEndpoints)
        every { authStateManager.exchangeAuthorizationCode(any(), any(), any()) } returns Unit
        loginActivityViewModel.authenticateToGetToken(mockk(), mockk())
        every { authStateManager.exchangeAuthorizationCode(any(), any(), any()) }
    }

    @Test
    fun getAuthRequestSuccess() {
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        val authRequestResult = Result.Success(mockk<AuthorizationRequest>())
        every { authStateManager.createRequestToAuth() } returns authRequestResult
        loginActivityViewModel.getAuthorizationRequest(authorizationEndpoints)
        Assert.assertEquals(authRequestResult, loginActivityViewModel.authRequestResult.value)
        verify { authStateManager.createRequestToAuth() }
    }

    @Test
    fun getAuthRequestError() {
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        val authRequestResult = Result.Error(Exception())
        every { authStateManager.createRequestToAuth() } returns authRequestResult
        loginActivityViewModel.getAuthorizationRequest(authorizationEndpoints)
        Assert.assertEquals(authRequestResult, loginActivityViewModel.authRequestResult.value)
        verify { authStateManager.createRequestToAuth() }
    }

    @Test
    fun saveToken() = runBlockingTest {
        coEvery { preferencesManager.saveToken(any()) } returns Unit
        loginActivityViewModel.saveToken("")
        coVerify { preferencesManager.saveToken(any()) }
    }

    @Test
    fun getAuthorizationEndpointsSuccess() = runBlockingTest {
        val result = Result.Success(mockk<AuthorizationEndpoints>())
        coEvery { useCases.getAuthorizationEndpoints() } returns result
        loginActivityViewModel.getAuthorizationEndpoints()
        Assert.assertEquals(result, loginActivityViewModel.authEndpointsResult.value)
        coVerify { useCases.getAuthorizationEndpoints() }
    }

    @Test
    fun getAuthorizationEndpointsError() = runBlockingTest {
        val result = Result.Error(Exception())
        coEvery { useCases.getAuthorizationEndpoints() } returns result
        loginActivityViewModel.getAuthorizationEndpoints()
        Assert.assertEquals(result, loginActivityViewModel.authEndpointsResult.value)
        coVerify { useCases.getAuthorizationEndpoints() }
    }

    @Test
    fun getDevicePasswordSuccess() = runBlockingTest {
        val result = Result.Success("")
        coEvery { useCases.getDevicePassword("") } returns result
        loginActivityViewModel.getDevicePassword("")
        Assert.assertEquals(result, loginActivityViewModel.devicePasswordResult.value)
        coVerify { useCases.getDevicePassword("") }
    }

    @Test
    fun getDevicePasswordError() = runBlockingTest {
        val result = Result.Error(Exception())
        coEvery { useCases.getDevicePassword("") } returns result
        loginActivityViewModel.getDevicePassword("")
        Assert.assertEquals(result, loginActivityViewModel.devicePasswordResult.value)
        coVerify { useCases.getDevicePassword("") }
    }
}
