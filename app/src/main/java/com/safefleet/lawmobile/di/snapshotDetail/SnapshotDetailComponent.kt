package com.safefleet.lawmobile.di.snapshotDetail

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [SnapshotDetailModule::class])
interface SnapshotDetailComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SnapshotDetailComponent
    }
}