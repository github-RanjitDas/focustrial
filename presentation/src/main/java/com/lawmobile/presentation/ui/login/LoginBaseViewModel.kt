package com.lawmobile.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

open class LoginBaseViewModel(
    private val getUserFromCameraUseCase: ValidatePasswordOfficerUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val isInstructionsOpen: LiveData<Boolean> get() = _isInstructionsOpen
    private val _isInstructionsOpen by lazy { MutableLiveData<Boolean>().apply { value = false } }

    val userFromCameraResult: LiveData<Result<DomainUser>> get() = _userFromCameraResult
    private val _userFromCameraResult by lazy { MediatorLiveData<Result<DomainUser>>() }

    fun setInstructionsOpen(isOpen: Boolean) {
        _isInstructionsOpen.value = isOpen
    }

    fun getUserFromCamera() {
        viewModelScope.launch(ioDispatcher) {
            _userFromCameraResult.postValue(getUserFromCameraUseCase.getUserInformation())
        }
    }
}
