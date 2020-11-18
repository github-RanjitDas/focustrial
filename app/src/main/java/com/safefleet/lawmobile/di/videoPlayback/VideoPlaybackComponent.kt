package com.safefleet.lawmobile.di.videoPlayback

import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [VideoPlaybackModule::class, AndroidInjectionModule::class])
interface VideoPlaybackComponent