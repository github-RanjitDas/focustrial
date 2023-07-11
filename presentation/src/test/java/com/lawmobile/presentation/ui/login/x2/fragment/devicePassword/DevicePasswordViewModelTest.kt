package com.lawmobile.presentation.ui.login.x2.fragment.devicePassword

import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.bluetooth.PasswordVerificationBleManager
import io.mockk.clearAllMocks
import io.mockk.mockk
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
class DevicePasswordViewModelTest {

    private val passwordVerificationBleManager: PasswordVerificationBleManager = mockk()
    private val viewModel = DevicePasswordViewModel(passwordVerificationBleManager)

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        clearAllMocks()
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetOfficerId() {
        Assert.assertEquals("", viewModel.officerId)
    }

    @Test
    fun testSetOfficerId() {
        val officerId = "123"
        viewModel.officerId = officerId
        Assert.assertEquals(officerId, viewModel.officerId)
    }

    @Test
    fun testGetOfficerPassword() {
        Assert.assertEquals("", viewModel.officerPassword)
    }

    @Test
    fun testSetOfficerPassword() {
        val password = "123"
        viewModel.officerPassword = password
        Assert.assertEquals(password, viewModel.officerPassword)
    }
}
