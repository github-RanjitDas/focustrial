package com.safefleet.lawmobile.di.fileList

import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [FileListModule::class, AndroidInjectionModule::class])
interface FileListComponent
