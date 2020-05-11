package com.safefleet.lawmobile.di

import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.live.LiveActivity
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.ui.videoPlayback.VideoPlaybackActivity
import com.safefleet.lawmobile.di.fileList.FileListModule
import com.safefleet.lawmobile.di.liveStreaming.LiveStreamingModule
import com.safefleet.lawmobile.di.login.LoginModule
import com.safefleet.lawmobile.di.snapshotDetail.SnapshotDetailModule
import com.safefleet.lawmobile.di.videoPlayback.VideoPlaybackModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModules {
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun contributeLogin(): LoginActivity

    @ContributesAndroidInjector(modules = [LiveStreamingModule::class])
    abstract fun contributeLiveActivity(): LiveActivity

    @ContributesAndroidInjector(modules = [VideoPlaybackModule::class])
    abstract fun contributeVideoPlayback(): VideoPlaybackActivity

    @ContributesAndroidInjector(modules = [FileListModule::class])
    abstract fun contributeFileListActivity(): FileListActivity

    @ContributesAndroidInjector(modules = [SnapshotDetailModule::class])
    abstract fun contributeSnapshotDetailActivity(): SnapshotDetailActivity

}