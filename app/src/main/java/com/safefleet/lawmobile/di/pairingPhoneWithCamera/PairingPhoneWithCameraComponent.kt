package com.safefleet.lawmobile.di.pairingPhoneWithCamera

import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [PairingPhoneWithCameraModule::class, AndroidInjectionModule::class])
interface PairingPhoneWithCameraComponent