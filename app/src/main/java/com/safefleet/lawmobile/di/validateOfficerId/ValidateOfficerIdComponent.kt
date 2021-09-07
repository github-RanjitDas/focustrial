package com.safefleet.lawmobile.di.validateOfficerId

import dagger.Subcomponent
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@Subcomponent(modules = [ValidateOfficerIdModule::class])
interface ValidateOfficerIdComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ValidateOfficerIdComponent
    }
}
