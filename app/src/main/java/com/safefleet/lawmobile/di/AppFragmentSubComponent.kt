package com.safefleet.lawmobile.di

import com.safefleet.lawmobile.di.pairingPhoneWithCamera.PairingPhoneWithCameraComponent
import com.safefleet.lawmobile.di.simpleList.SimpleListComponent
import com.safefleet.lawmobile.di.thumbnailList.ThumbnailListComponent
import com.safefleet.lawmobile.di.validatePasswordOfficer.ValidatePasswordOfficerComponent
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module(
    subcomponents = [
        PairingPhoneWithCameraComponent::class,
        ValidatePasswordOfficerComponent::class,
        SimpleListComponent::class,
        ThumbnailListComponent::class
    ]
)
class AppFragmentSubComponent
