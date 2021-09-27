package com.safefleet.lawmobile.di.audioDetail

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [AudioDetailModule::class])
interface AudioDetailComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AudioDetailComponent
    }
}
