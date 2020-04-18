package com.lawmobile.presentation.ui.validatePasswordOfficer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entity.DomainUser
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class ValidatePasswordOfficerViewModel @Inject constructor(private val validatePasswordOfficerUseCase: ValidatePasswordOfficerUseCase) :
    BaseViewModel() {

    private val domainUserMediator by lazy { MediatorLiveData<DomainUser>() }
    val domainUser: LiveData<DomainUser> get() = domainUserMediator

    private val errorDomainUserMediator by lazy { MediatorLiveData<String>() }
    val errorDomainUser: LiveData<String> get() = errorDomainUserMediator

    fun getUserInformation() {
        viewModelScope.launch {
            when (val result = validatePasswordOfficerUseCase.getUserInformation()) {
                is Result.Success -> domainUserMediator.postValue(result.data)
                is Result.Error -> errorDomainUserMediator.postValue(result.exception.message)
            }
        }
    }

}