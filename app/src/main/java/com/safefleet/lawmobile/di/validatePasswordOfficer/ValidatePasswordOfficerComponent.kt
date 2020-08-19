package com.safefleet.lawmobile.di.validatePasswordOfficer

import dagger.Subcomponent
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@Subcomponent(modules = [ValidatePasswordOfficerModule::class])
interface ValidatePasswordOfficerComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ValidatePasswordOfficerComponent
    }
}