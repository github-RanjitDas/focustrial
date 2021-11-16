package com.lawmobile.domain.usecase

import com.lawmobile.domain.usecase.getAuthorizationEndpoints.GetAuthorizationEndpoints
import com.lawmobile.domain.usecase.getDevicePassword.GetDevicePassword
import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCamera

data class LoginUseCases(
    val getAuthorizationEndpoints: GetAuthorizationEndpoints,
    val getDevicePassword: GetDevicePassword,
    val getUserFromCamera: GetUserFromCamera
)
