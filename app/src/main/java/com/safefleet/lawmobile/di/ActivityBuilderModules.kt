package com.safefleet.lawmobile.di

import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.di.login.LoginModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModules {
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun contributeLogin(): LoginActivity
}