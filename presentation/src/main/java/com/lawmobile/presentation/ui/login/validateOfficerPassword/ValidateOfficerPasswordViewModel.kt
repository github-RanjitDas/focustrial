package com.lawmobile.presentation.ui.login.validateOfficerPassword

import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ValidateOfficerPasswordViewModel @Inject constructor(
    private val typeOfCameraUseCase: TypeOfCameraUseCase
) : BaseViewModel() {
    fun setCameraType() {
        viewModelScope.launch {
            typeOfCameraUseCase.getTypeOfCamera().doIfSuccess { CameraInfo.setCamera(it) }
        }
    }
}
