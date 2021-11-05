package com.safefleet.lawmobile.di.authorization

import dagger.Subcomponent
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@Subcomponent(modules = [AuthorizationEndpointsModule::class])
interface AuthorizationEndpointsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthorizationEndpointsComponent
    }
}
