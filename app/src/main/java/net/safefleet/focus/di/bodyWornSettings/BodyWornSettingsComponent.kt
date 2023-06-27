package net.safefleet.focus.di.bodyWornSettings

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [BodyWornSettingsModule::class])
interface BodyWornSettingsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): BodyWornSettingsComponent
    }
}
