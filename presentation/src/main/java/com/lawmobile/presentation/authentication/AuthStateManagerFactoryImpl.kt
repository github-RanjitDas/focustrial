package com.lawmobile.presentation.authentication

import android.content.SharedPreferences
import android.net.Uri
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.presentation.BuildConfig
import com.safefleet.mobile.authentication.AuthStateManager
import com.safefleet.mobile.authentication.entities.ConnectionParameters
import com.safefleet.mobile.authentication.sso.AuthStateManagerImpl
import net.openid.appauth.AuthorizationService

class AuthStateManagerFactoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val authorizationService: AuthorizationService
) : AuthStateManagerFactory {

    override fun create(authorizationEndpoints: AuthorizationEndpoints): AuthStateManager {
        val connectionParameters = buildConnectionParameters(authorizationEndpoints)
        return AuthStateManagerImpl(sharedPreferences, connectionParameters, authorizationService)
    }

    private fun buildConnectionParameters(
        authorizationEndpoints: AuthorizationEndpoints
    ): ConnectionParameters {
        return ConnectionParameters(
            redirectURI = Uri.parse(BuildConfig.SSO_CALLBACK),
            clientID = BuildConfig.SSO_CLIENT_ID,
            authorizationScopes = BuildConfig.SSO_AUTH_SCOPES.toList(),
            authorizationEndpointURI = Uri.parse(authorizationEndpoints.authorizationEndpoint),
            tokenEndPointURI = Uri.parse(authorizationEndpoints.tokenEndpoint)
        )
    }
}
