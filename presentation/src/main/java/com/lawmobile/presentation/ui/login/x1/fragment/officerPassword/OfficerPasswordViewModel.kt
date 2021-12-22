package com.lawmobile.presentation.ui.login.x1.fragment.officerPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfficerPasswordViewModel @Inject constructor(
    private val validatePasswordOfficerUseCase: ValidatePasswordOfficerUseCase
) : BaseViewModel() {

    private val domainUserMediator by lazy { MediatorLiveData<Result<DomainUser>>() }
    val domainUserLiveData: LiveData<Result<DomainUser>> get() = domainUserMediator

    private val officerPassword by lazy { MutableLiveData<String>() }

    fun setOfficerPassword(password: String) {
        officerPassword.value = password
    }

    fun getOfficerPassword(): String = officerPassword.value ?: ""

    fun getUserInformation() {
        viewModelScope.launch {
            domainUserMediator.postValue(
                validatePasswordOfficerUseCase.getUserInformation()
            )
        }
    }
}
