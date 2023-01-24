package com.lawmobile.presentation.ui.bodyWornSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.ParametersBodyWornSettings
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.domain.usecase.bodyWornSettings.BodyWornSettingsUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BodyWornSettingsViewModel @Inject constructor(
    private val bodyWornSettingsUseCase: BodyWornSettingsUseCase
) : BaseViewModel() {

    private val _bodyCameraSettings = MediatorLiveData<Result<ParametersBodyWornSettings>>()
    val bodyCameraSettings: LiveData<Result<ParametersBodyWornSettings>> get() = _bodyCameraSettings

    private val changeStatusSettingMediator: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val changeStatusSettingLiveData: LiveData<Result<Unit>> get() = changeStatusSettingMediator

    fun getBodyWornSettings() {
        viewModelScope.launch {
            _bodyCameraSettings.postValue(bodyWornSettingsUseCase.getParametersEnable())
        }
    }

    suspend fun getBodyCameraSettings() {
        _bodyCameraSettings.postValue(bodyWornSettingsUseCase.getParametersEnable())
    }

    fun getBodyCameraSettingsInBg() {
        viewModelScope.launch(Dispatchers.IO) {
            _bodyCameraSettings.postValue(bodyWornSettingsUseCase.getParametersEnable())
        }
    }

    fun changeBodyWornSetting(typeOfSettings: TypesOfBodyWornSettings, isEnable: Boolean) {
        when (typeOfSettings) {
            TypesOfBodyWornSettings.CovertMode -> CameraInfo.isCovertModeEnable = isEnable
            TypesOfBodyWornSettings.Bluetooth -> CameraInfo.isBluetoothEnable = isEnable
        }
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
