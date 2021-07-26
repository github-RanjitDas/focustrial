package com.safefleet.lawmobile.di.typeOfCamera

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [TypeOfCameraModule::class])
interface TypeOfCameraComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TypeOfCameraComponent
    }
}
