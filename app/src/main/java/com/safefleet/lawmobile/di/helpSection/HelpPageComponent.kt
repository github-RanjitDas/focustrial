package com.safefleet.lawmobile.di.helpSection

import com.safefleet.lawmobile.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [HelpPageModule::class])
interface HelpPageComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): HelpPageComponent
    }
}