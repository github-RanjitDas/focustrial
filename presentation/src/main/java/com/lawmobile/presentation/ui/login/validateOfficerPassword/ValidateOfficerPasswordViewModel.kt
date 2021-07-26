package com.lawmobile.presentation.ui.login.validateOfficerPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCase
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.CameraHelper
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ValidateOfficerPasswordViewModel @Inject constructor(
    private val validatePasswordOfficerUseCase: ValidatePasswordOfficerUseCase,
    private val typeOfCameraUseCase: TypeOfCameraUseCase,
    private val cameraHelper: CameraHelper
) : BaseViewModel() {

    private val domainUserMediator by lazy { MediatorLiveData<Result<DomainUser>>() }
    val domainUserLiveData: LiveData<Result<DomainUser>> get() = domainUserMediator

    fun createSingletonCameraHelper() {
        CameraHelper.setInstance(cameraHelper)
    }

    fun getUserInformation() {
        viewModelScope.launch {
            domainUserMediator.postValue(
                validatePasswordOfficerUseCase.getUserInformation()
            )
        }
    }

    fun setCameraType() {
        viewModelScope.launch {
            val response = typeOfCameraUseCase.getTypeOfCamera()
            response.doIfSuccess {
                CameraInfo.setCamera(it)
            }
        }
    }
}
