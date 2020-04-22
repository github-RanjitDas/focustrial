package com.safefleet.lawmobile.di

import com.lawmobile.presentation.ui.live.LiveActivity
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.di.liveStreaming.LiveStreamingModule
import com.safefleet.lawmobile.di.login.LoginModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModules {
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun contributeLogin(): LoginActivity

    @ContributesAndroidInjector(modules = [LiveStreamingModule::class])
    abstract fun contributeLiveActivity(): LiveActivity
}