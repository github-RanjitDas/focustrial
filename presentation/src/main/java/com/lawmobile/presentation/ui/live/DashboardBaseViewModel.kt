package com.lawmobile.presentation.ui.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.ui.live.model.DashboardState

class DashboardBaseViewModel : BaseViewModel() {
    val dashboardState: LiveData<DashboardState> get() = _dashboardState
    private val _dashboardState =
        MutableLiveData<DashboardState>().apply { value = DashboardState.Default }

    fun getDashboardState(): DashboardState = dashboardState.value ?: DashboardState.Default

    fun setDashboardState(state: DashboardState) {
        _dashboardState.value = state
    }
}
