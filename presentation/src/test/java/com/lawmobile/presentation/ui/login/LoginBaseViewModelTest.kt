package com.lawmobile.presentation.ui.login

import android.bluetooth.BluetoothAdapter
import android.os.Handler
import android.os.Looper
import com.lawmobile.domain.usecase.LoginUseCases
import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCamera
import com.lawmobile.domain.utils.PreferencesManager
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.authentication.AuthStateManagerFactory
import com.lawmobile.presentation.bluetooth.FetchConfigBleManager
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.login.shared.PairingViewModelTest
import com.lawmobile.presentation.ui.login.state.LoginState
import com.lawmobile.presentation.ui.login.x2.LoginX2ViewModel
import com.safefleet.mobile.authentication.AuthStateManager
import com.safefleet.mobile.kotlin_commons.helpers.network_manager.SimpleNetworkManager
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
    private val useCases: LoginUseCases = mockk(relaxed = true)

    private val authStateManager: AuthStateManager = mockk()

    private val authStateManagerFactory: AuthStateManagerFactory = mockk {
        every { create(any()) } returns authStateManager
    }
    private val preferencesManager: PreferencesManager = mockk()
    private val bleManager: FetchConfigBleManager = mockk()
    private val bleAdapter: BluetoothAdapter = mockk()
    private val simpleNetworkManager: SimpleNetworkManager = mockk()
    private val baseViewModel =
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
    fun suggestWiFiNetwork() {
        every { wifiHelper.suggestWiFiNetwork(any(), any(), any(), any()) } returns Unit
        val handler = Handler(Looper.getMainLooper())
        baseViewModel.suggestWiFiNetwork(handler, "", "") {}
        verify { wifiHelper.suggestWiFiNetwork(any(), any(), any(), any()) }
    }

    @Test
    fun setLoginState() {
        val state = LoginState.PairingResult
        baseViewModel.setLoginState(state)
        Assert.assertEquals(state, baseViewModel.loginState.value)
    }
}
