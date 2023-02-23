package com.lawmobile.presentation.ui.login.x2

import android.bluetooth.BluetoothAdapter
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.usecase.LoginUseCases
import com.lawmobile.domain.utils.PreferencesManager
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.authentication.AuthStateManagerFactory
import com.lawmobile.presentation.bluetooth.FetchConfigBleManager
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.login.shared.PairingViewModelTest
import com.lawmobile.presentation.ui.login.state.LoginState
import com.safefleet.mobile.authentication.AuthStateManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.network_manager.SimpleNetworkManager
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
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(InstantExecutorExtension::class)
internal class LoginX2ViewModelTest {
    private val useCases: LoginUseCases = mockk(relaxed = true)
    private val dispatcher = TestCoroutineDispatcher()
    private val authStateManager: AuthStateManager = mockk()
    private val wifiHelper: WifiHelper = mockk {
        every { isEqualsValueWithSSID(PairingViewModelTest.DEFAULT_SSID) } returns true
        every { isEqualsValueWithSSID("X") } returns false
    }
    private val authStateManagerFactory: AuthStateManagerFactory = mockk {
        every { create(any()) } returns authStateManager
    }
    private val preferencesManager: PreferencesManager = mockk()
    private val bleManager: FetchConfigBleManager = mockk()
    private val bleAdapter: BluetoothAdapter = mockk()
    private val simpleNetworkManager: SimpleNetworkManager = mockk()
    private val viewModel =
        LoginX2ViewModel(
            useCases,
            authStateManagerFactory,
            preferencesManager,
            dispatcher,
            bleManager,
            wifiHelper,
            bleAdapter,
            simpleNetworkManager
        )

    @BeforeEach
    fun setUp() {
        clearMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun isUserAuthorizedTrue() {
        every { authStateManager.isCurrentAuthStateAuthorized() } returns true
        viewModel.getAuthorizationRequest(mockk(relaxed = true))
        Assert.assertTrue(viewModel.isUserAuthorized())
        verify { authStateManager.isCurrentAuthStateAuthorized() }
    }

    @Test
    fun isUserAuthorizedFalse() {
        every { authStateManager.isCurrentAuthStateAuthorized() } returns false
        viewModel.getAuthorizationRequest(mockk(relaxed = true))
        Assert.assertFalse(viewModel.isUserAuthorized())
        verify { authStateManager.isCurrentAuthStateAuthorized() }
    }

    @Test
    fun authenticateToGetTokenFlow() {
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        viewModel.getAuthorizationRequest(authorizationEndpoints)
        every { authStateManager.exchangeAuthorizationCode(any(), any(), any()) } returns Unit
        viewModel.authenticateToGetToken(mockk(), mockk())
        every { authStateManager.exchangeAuthorizationCode(any(), any(), any()) }
    }

    @Test
    fun getAuthRequestSuccess() {
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        val authRequestResult = Result.Success(mockk<AuthorizationRequest>())
        every { authStateManager.createRequestToAuth() } returns authRequestResult
        viewModel.getAuthorizationRequest(authorizationEndpoints)
        Assert.assertEquals(authRequestResult, viewModel.authRequestResult.value)
        verify { authStateManager.createRequestToAuth() }
    }

    @Test
    fun getAuthRequestError() {
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        val authRequestResult = Result.Error(Exception())
        every { authStateManager.createRequestToAuth() } returns authRequestResult
        viewModel.getAuthorizationRequest(authorizationEndpoints)
        Assert.assertEquals(authRequestResult, viewModel.authRequestResult.value)
        verify { authStateManager.createRequestToAuth() }
    }

    @Test
    fun saveToken() = runBlockingTest {
        coEvery { preferencesManager.saveToken(any()) } returns Unit
        viewModel.saveToken("")
        coVerify { preferencesManager.saveToken(any()) }
    }

    @Test
    fun getAuthorizationEndpointsSuccess() = runBlockingTest {
        val result = Result.Success(mockk<AuthorizationEndpoints>())
        coEvery { useCases.getAuthorizationEndpoints() } returns result
        viewModel.getAuthorizationEndpoints()
        Assert.assertEquals(result, viewModel.authEndpointsResult.value)
        coVerify { useCases.getAuthorizationEndpoints() }
    }

    @Test
    fun getAuthorizationEndpointsError() = runBlockingTest {
        val result = Result.Error(Exception())
        coEvery { useCases.getAuthorizationEndpoints() } returns result
        viewModel.getAuthorizationEndpoints()
        Assert.assertEquals(result, viewModel.authEndpointsResult.value)
        coVerify { useCases.getAuthorizationEndpoints() }
    }

    @Test
    fun getDevicePasswordSuccess() = runBlockingTest {
        val result = Result.Success("")
        coEvery { useCases.getDevicePassword("") } returns result
        viewModel.getDevicePassword("")
        Assert.assertEquals(result, viewModel.devicePasswordResult.value)
        coVerify { useCases.getDevicePassword("") }
    }

    @Test
    fun getDevicePasswordError() = runBlockingTest {
        val result = Result.Error(Exception())
        coEvery { useCases.getDevicePassword("") } returns result
        viewModel.getDevicePassword("")
        Assert.assertEquals(result, viewModel.devicePasswordResult.value)
        coVerify { useCases.getDevicePassword("") }
    }

    @Test
    fun getLoginState() {
        Assert.assertEquals(LoginState.X2.OfficerId, viewModel.getLoginState())
    }

    @Test
    fun getOfficerId() {
        Assert.assertEquals("", viewModel.officerId)
    }

    @Test
    fun setOfficerId() {
        val officerId = "123"
        viewModel.officerId = officerId
        Assert.assertEquals(officerId, viewModel.officerId)
    }
}
