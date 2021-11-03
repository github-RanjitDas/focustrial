package com.lawmobile.presentation.utils

import com.safefleet.mobile.authentication.AuthStateManager

interface AuthStateManagerFactory {
    fun create(): AuthStateManager
}
