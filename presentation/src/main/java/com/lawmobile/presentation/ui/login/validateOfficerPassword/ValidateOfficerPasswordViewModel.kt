package com.lawmobile.presentation.ui.login.validateOfficerPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.CameraHelper
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class ValidateOfficerPasswordViewModel @Inject constructor(
    private val validatePasswordOfficerUseCase: ValidatePasswordOfficerUseCase,
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
}