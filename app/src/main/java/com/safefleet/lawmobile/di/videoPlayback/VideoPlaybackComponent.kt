package com.safefleet.lawmobile.di.videoPlayback

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [VideoPlaybackModule::class])
interface VideoPlaybackComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): VideoPlaybackComponent
    }
}
