package com.safefleet.lawmobile.di.fileListItemDetail

import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [SnapshotItemDetailModule::class, AndroidInjectionModule::class])
interface SnapshotItemDetailComponent
