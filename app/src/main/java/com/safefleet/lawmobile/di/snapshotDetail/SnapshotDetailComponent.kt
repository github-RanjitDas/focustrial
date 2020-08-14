package com.safefleet.lawmobile.di.snapshotDetail

import com.safefleet.lawmobile.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [SnapshotDetailModule::class])
interface SnapshotDetailComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SnapshotDetailComponent
    }
}