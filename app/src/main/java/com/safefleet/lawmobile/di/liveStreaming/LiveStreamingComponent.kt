package com.safefleet.lawmobile.di.liveStreaming

import com.safefleet.lawmobile.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [LiveStreamingModule::class])
interface LiveStreamingComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LiveStreamingComponent
    }
}