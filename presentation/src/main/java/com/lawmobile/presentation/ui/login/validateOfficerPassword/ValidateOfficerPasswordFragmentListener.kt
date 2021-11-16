package com.lawmobile.presentation.ui.login.validateOfficerPassword

import com.lawmobile.domain.entities.User

interface ValidateOfficerPasswordFragmentListener {
    var user: User?
    fun onPasswordValidationResult(isValid: Boolean)
    fun onEmptyUserInformation()
}
