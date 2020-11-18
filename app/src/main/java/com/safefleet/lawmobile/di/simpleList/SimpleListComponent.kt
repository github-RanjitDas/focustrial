package com.safefleet.lawmobile.di.simpleList

import dagger.Subcomponent
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@Subcomponent(modules = [SimpleListModule::class])
interface SimpleListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SimpleListComponent
    }
}