package net.safefleet.focus.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.safefleet.focus.di.pairingPhoneWithCamera.PairingPhoneWithCameraComponent
import net.safefleet.focus.di.simpleList.SimpleListComponent
import net.safefleet.focus.di.thumbnailList.ThumbnailListComponent

@InstallIn(SingletonComponent::class)
@Module(
    subcomponents = [
        PairingPhoneWithCameraComponent::class,
        SimpleListComponent::class,
        ThumbnailListComponent::class
    ]
)
class AppFragmentSubComponent
