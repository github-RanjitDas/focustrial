package com.safefleet.lawmobile.di.linkSnapshotsToVideo

import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [LinkSnapshotsModule::class, AndroidInjectionModule::class])
interface LinkSnapshotsComponent