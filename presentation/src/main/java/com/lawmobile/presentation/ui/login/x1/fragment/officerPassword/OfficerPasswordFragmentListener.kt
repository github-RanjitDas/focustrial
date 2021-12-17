package com.lawmobile.presentation.ui.login.x1.fragment.officerPassword

import com.lawmobile.domain.entities.User

interface OfficerPasswordFragmentListener {
    var user: User?
    fun onPasswordValidationResult(isValid: Boolean)
    fun onEmptyUserInformation()
}
