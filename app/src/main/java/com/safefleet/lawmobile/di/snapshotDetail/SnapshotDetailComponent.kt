package com.safefleet.lawmobile.di.snapshotDetail

import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [SnapshotDetailModule::class, AndroidInjectionModule::class])
interface SnapshotDetailComponent