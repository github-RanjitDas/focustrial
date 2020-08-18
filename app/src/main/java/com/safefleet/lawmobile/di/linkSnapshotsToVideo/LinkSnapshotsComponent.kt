package com.safefleet.lawmobile.di.linkSnapshotsToVideo

import com.safefleet.lawmobile.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [LinkSnapshotsModule::class])
interface LinkSnapshotsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LinkSnapshotsComponent
    }
}