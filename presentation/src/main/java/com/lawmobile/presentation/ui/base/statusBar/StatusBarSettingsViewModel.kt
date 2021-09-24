package com.lawmobile.presentation.ui.base.statusBar

import com.lawmobile.domain.usecase.bodyWornSettings.BodyWornSettingsUseCase
import com.lawmobile.presentation.ui.bodyWornSettings.BodyWornSettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatusBarSettingsViewModel @Inject constructor(
    bodyWornSettingsUseCase: BodyWornSettingsUseCase
) : BodyWornSettingsViewModel(bodyWornSettingsUseCase)
