package com.lawmobile.presentation.ui.login.validateOfficerPassword

import com.lawmobile.domain.entities.DomainUser

interface ValidateOfficerPasswordFragmentListener {
    var domainUser: DomainUser?
    fun onPasswordValidationResult(isValid: Boolean)
    fun onEmptyUserInformation()
}
