package com.lawmobile.presentation.ui.live

import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.ui.live.state.DashboardState
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(InstantExecutorExtension::class)
internal class DashboardBaseViewModelTest {

    private val viewModel by lazy { DashboardBaseViewModel() }

    @Test
    fun getDashboardStateWhenNull() {
        Assert.assertEquals(DashboardState.Default, viewModel.getDashboardState())
    }

    @Test
    fun setAndGetDashboardState() {
        viewModel.setDashboardState(DashboardState.Fullscreen)
        Assert.assertEquals(DashboardState.Fullscreen, viewModel.getDashboardState())
    }
}
