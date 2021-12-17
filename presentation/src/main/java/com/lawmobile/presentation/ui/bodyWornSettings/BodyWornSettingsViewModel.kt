package com.lawmobile.presentation.ui.bodyWornSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.domain.usecase.bodyWornSettings.BodyWornSettingsUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BodyWornSettingsViewModel @Inject constructor(
    private val bodyWornSettingsUseCase: BodyWornSettingsUseCase
) : BaseViewModel() {

    private val resultParametersSettingsMediator: MediatorLiveData<Result<ParametersBodyWornSettings>> =
        MediatorLiveData()
    val bodyWornSettingsLiveData: LiveData<Result<ParametersBodyWornSettings>> get() = resultParametersSettingsMediator

    private val changeStatusSettingMediator: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val changeStatusSettingLiveData: LiveData<Result<Unit>> get() = changeStatusSettingMediator

    fun getBodyWornSettings() {
        viewModelScope.launch {
            resultParametersSettingsMediator.postValue(bodyWornSettingsUseCase.getParametersEnable())
        }
    }

    fun changeBodyWornSetting(typeOfSettings: TypesOfBodyWornSettings, isEnable: Boolean) {
        viewModelScope.launch {
            changeStatusSettingMediator.postValue(
                bodyWornSettingsUseCase.changeStatusSettings(
                    typeOfSettings,
                    isEnable
                )
            )
        }
    }
}
