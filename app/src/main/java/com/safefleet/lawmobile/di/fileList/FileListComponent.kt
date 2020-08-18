package com.safefleet.lawmobile.di.fileList

import com.safefleet.lawmobile.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [FileListModule::class])
interface FileListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): FileListComponent
    }
}
