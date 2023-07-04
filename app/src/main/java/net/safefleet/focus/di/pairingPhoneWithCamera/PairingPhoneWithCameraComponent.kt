package net.safefleet.focus.di.pairingPhoneWithCamera

import dagger.Subcomponent
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@Subcomponent(modules = [PairingPhoneWithCameraModule::class])
interface PairingPhoneWithCameraComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PairingPhoneWithCameraComponent
    }
}
