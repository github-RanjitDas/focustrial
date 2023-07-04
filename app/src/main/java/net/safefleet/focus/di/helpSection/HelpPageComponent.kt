package net.safefleet.focus.di.helpSection

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [HelpPageModule::class])
interface HelpPageComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): HelpPageComponent
    }
}
