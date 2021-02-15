package com.safefleet.lawmobile.di.liveStreaming

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [LiveStreamingModule::class])
interface LiveStreamingComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LiveStreamingComponent
    }
}
