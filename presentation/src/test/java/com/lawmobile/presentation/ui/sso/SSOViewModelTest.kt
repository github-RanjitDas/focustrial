package com.lawmobile.presentation.ui.sso

import com.safefleet.mobile.android_commons.helpers.network_manager.ListenableNetworkManager
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class SSOViewModelTest {

    private val networkManager: ListenableNetworkManager = mockk()
    private val viewModel = SSOViewModel(networkManager)

    @Test
    fun getNetworkManager() {
        Assert.assertEquals(networkManager, viewModel.getNetworkManager())
    }
}
