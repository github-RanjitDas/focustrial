package net.safefleet.focus.di.fileList

import dagger.Subcomponent
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Subcomponent(modules = [FileListModule::class])
interface FileListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): FileListComponent
    }
}
