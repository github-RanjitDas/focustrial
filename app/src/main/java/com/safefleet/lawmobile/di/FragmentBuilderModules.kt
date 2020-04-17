package com.safefleet.lawmobile.di

import com.lawmobile.presentation.ui.pairingPhoneWithCamera.PairingPhoneWithCameraFragment
import com.lawmobile.presentation.ui.validatePasswordOfficer.ValidatePasswordOfficerFragment
import com.safefleet.lawmobile.di.pairingPhoneWithCamera.PairingPhoneWithCameraModule
import com.safefleet.lawmobile.di.validatePasswordOfficer.ValidatePasswordOfficerModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModules {

    @ContributesAndroidInjector(modules = [PairingPhoneWithCameraModule::class])
    abstract fun contributePairingPhoneWithCameraFragment(): PairingPhoneWithCameraFragment

    @ContributesAndroidInjector(modules = [ValidatePasswordOfficerModule::class])
    abstract fun contributeValidatePasswordOfficer():ValidatePasswordOfficerFragment
}