package com.lawmobile.presentation.ui.login.shared

interface OfficerPassword {
    var passwordFromCamera: String
    var onEmptyPassword: (() -> Unit)?
}
