package com.safefleet.lawmobile.di.liveStreaming

import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [LiveStreamingModule::class, AndroidInjectionModule::class])
interface LiveStreamingComponent