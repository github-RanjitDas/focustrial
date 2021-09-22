package com.lawmobile.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validateOfficerPassword.ValidateOfficerPasswordUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val validateOfficerPasswordUseCase: ValidateOfficerPasswordUseCase,
) : BaseViewModel() {

    val userInformationResult: LiveData<Result<DomainUser>> get() = _userInformationResult
    private val _userInformationResult by lazy { MediatorLiveData<Result<DomainUser>>() }

    fun getUserInformation() {
        viewModelScope.launch {
            _userInformationResult.postValue(
                validateOfficerPasswordUseCase.getUserInformation()
            )
        }
    }
}
