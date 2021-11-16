package com.lawmobile.presentation.authentication

import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.safefleet.mobile.authentication.AuthStateManager

interface AuthStateManagerFactory {
    fun create(authorizationEndpoints: AuthorizationEndpoints): AuthStateManager
}
