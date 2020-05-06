package com.safefleet.lawmobile.di

import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.fileListItemDetail.SnapshotItemDetailActivity
import com.lawmobile.presentation.ui.live.LiveActivity
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.di.fileList.FileListModule
import com.safefleet.lawmobile.di.fileListItemDetail.SnapshotItemDetailModule
import com.safefleet.lawmobile.di.liveStreaming.LiveStreamingModule
import com.safefleet.lawmobile.di.login.LoginModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModules {
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun contributeLogin(): LoginActivity

    @ContributesAndroidInjector(modules = [LiveStreamingModule::class])
    abstract fun contributeLiveActivity(): LiveActivity

    @ContributesAndroidInjector(modules = [FileListModule::class])
    abstract fun contributeFileListActivity(): FileListActivity

    @ContributesAndroidInjector(modules = [SnapshotItemDetailModule::class])
    abstract fun contributeSnapshotItemDetailActivity(): SnapshotItemDetailActivity
}