package com.lawmobile.presentation.ui.login.x1

import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCamera
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.login.state.LoginState
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(InstantExecutorExtension::class)
internal class LoginX1ViewModelTest {

    private val useCase: GetUserFromCamera = mockk()
    private val wifiHelper: WifiHelper = mockk()
    private val dispatcher = TestCoroutineDispatcher()
    private val viewModel = LoginX1ViewModel(useCase, wifiHelper, dispatcher)

    @Test
    fun getOfficerPasswordFromCameraWhenNull() {
        Assert.assertEquals("", viewModel.officerPasswordFromCamera)
    }

    @Test
    fun setOfficerPasswordFromCamera() {
        val password = "123"
        viewModel.officerPasswordFromCamera = password
        Assert.assertEquals(password, viewModel.officerPasswordFromCamera)
    }

    @Test
    fun getLoginState() {
        Assert.assertEquals(LoginState.X1.StartPairing, viewModel.getLoginState())
    }
}
