package net.safefleet.focus.di.authorization

import dagger.Subcomponent
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@Subcomponent(modules = [AuthorizationModule::class])
interface AuthorizationComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthorizationComponent
    }
}
