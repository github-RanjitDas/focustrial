package com.lawmobile.presentation.ui.login.validateOfficerId

import android.bluetooth.BluetoothAdapter
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.android_commons.helpers.network_manager.ListenableNetworkManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class
ValidateOfficerIdViewModelTest {

    private val simpleNetworkManager = mockk<ListenableNetworkManager>(relaxed = true)
    private val bluetoothAdapter = mockk<BluetoothAdapter>(relaxed = true)
    private val dispatcher = TestCoroutineDispatcher()
    private val viewModel = ValidateOfficerIdViewModel(simpleNetworkManager, dispatcher, bluetoothAdapter)

    @Test
    fun verifyInternetConnection() = runBlockingTest {
        coEvery { simpleNetworkManager.verifyInternetConnection(any()) } just Runs
        viewModel.verifyInternetConnection { }
        coVerify { simpleNetworkManager.verifyInternetConnection(any()) }
    }

    @Test
    fun verifyBluetoothConnectionTrue() = runBlockingTest {
        coEvery { bluetoothAdapter.isEnabled } returns true
        viewModel.verifyBluetoothConnection {
            Assert.assertTrue(it)
        }
        coVerify { bluetoothAdapter.isEnabled }
    }

    @Test
    fun verifyBluetoothConnectionFalse() = runBlockingTest {
        coEvery { bluetoothAdapter.isEnabled } returns false
        viewModel.verifyBluetoothConnection {
            Assert.assertFalse(it)
        }
        coVerify { bluetoothAdapter.isEnabled }
    }
}
