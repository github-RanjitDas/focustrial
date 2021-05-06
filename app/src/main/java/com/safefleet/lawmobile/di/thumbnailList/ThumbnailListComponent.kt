package com.safefleet.lawmobile.di.thumbnailList

import dagger.Subcomponent
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@Subcomponent(modules = [ThumbnailListModule::class])
interface ThumbnailListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ThumbnailListComponent
    }
}
