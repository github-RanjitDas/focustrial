package com.safefleet.lawmobile.di.videoPlayback

import com.safefleet.lawmobile.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [VideoPlaybackModule::class])
interface VideoPlaybackComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): VideoPlaybackComponent
    }
}