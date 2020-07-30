package com.safefleet.lawmobile.di

import com.lawmobile.presentation.ui.fileList.SnapshotListFragment
import com.lawmobile.presentation.ui.fileList.VideoListFragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.StartPairingFragment
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.PairingResultFragment
import com.lawmobile.presentation.ui.login.validateOfficerPassword.ValidateOfficerPasswordFragment
import com.safefleet.lawmobile.di.pairingPhoneWithCamera.PairingPhoneWithCameraModule
import com.safefleet.lawmobile.di.validatePasswordOfficer.ValidatePasswordOfficerModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModules {

    @ContributesAndroidInjector(modules = [PairingPhoneWithCameraModule::class])
    abstract fun contributePairingPhoneWithCameraFragment(): StartPairingFragment

    @ContributesAndroidInjector(modules = [ValidatePasswordOfficerModule::class])
    abstract fun contributeValidatePasswordOfficer(): ValidateOfficerPasswordFragment

    @ContributesAndroidInjector(modules = [PairingPhoneWithCameraModule::class])
    abstract fun contributePairingResultFragment(): PairingResultFragment

    @ContributesAndroidInjector
    abstract fun contributeSnapshotList(): SnapshotListFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoList(): VideoListFragment
}