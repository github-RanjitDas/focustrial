package com.lawmobile.presentation.utils

import android.content.SharedPreferences
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.lawmobile.presentation.BuildConfig
import com.safefleet.mobile.authentication.AuthStateManager
import com.safefleet.mobile.authentication.entities.ConnectionParameters
import com.safefleet.mobile.authentication.sso.AuthStateManagerImpl
import net.openid.appauth.AuthorizationService

class AuthStateManagerFactoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val dataStore: DataStore<Preferences>,
    private val authorizationService: AuthorizationService
) : AuthStateManagerFactory {

    override fun create(): AuthStateManager {
        val connectionParameters = buildConnectionParameters()
        return AuthStateManagerImpl(sharedPreferences, connectionParameters, authorizationService)
    }

    private fun buildConnectionParameters(): ConnectionParameters {
        // please retrieve endpoint urls from preferences data store when obtain them from discovery url
        dataStore
        return ConnectionParameters(
            redirectURI = Uri.parse(BuildConfig.SSO_CALLBACK),
            clientID = BuildConfig.SSO_CLIENT_ID,
            authorizationScopes = BuildConfig.SSO_AUTH_SCOPES.toList(),
            authorizationEndpointURI = Uri.parse("https://dev-id.safefleetcloud.com/connect/authorize"),
            tokenEndPointURI = Uri.parse("https://dev-id.safefleetcloud.com/connect/token")
        )
    }
}
