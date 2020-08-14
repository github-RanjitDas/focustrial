package com.safefleet.lawmobile.di.login

import com.safefleet.lawmobile.di.ActivityScope
import dagger.Subcomponent


@ActivityScope
@Subcomponent(modules = [LoginModule::class])
interface LoginComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }
}