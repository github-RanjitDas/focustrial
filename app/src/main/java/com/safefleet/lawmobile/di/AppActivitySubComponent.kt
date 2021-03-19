package com.safefleet.lawmobile.di

import com.safefleet.lawmobile.di.bodyWornDiagnosis.BodyWornDiagnosisComponent
import com.safefleet.lawmobile.di.fileList.FileListComponent
import com.safefleet.lawmobile.di.helpSection.HelpPageComponent
import com.safefleet.lawmobile.di.liveStreaming.LiveStreamingComponent
import com.safefleet.lawmobile.di.login.LoginComponent
import com.safefleet.lawmobile.di.snapshotDetail.SnapshotDetailComponent
import com.safefleet.lawmobile.di.videoPlayback.VideoPlaybackComponent
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(
    subcomponents = [
        FileListComponent::class,
        HelpPageComponent::class,
        LiveStreamingComponent::class,
        LoginComponent::class,
        SnapshotDetailComponent::class,
        VideoPlaybackComponent::class,
        BodyWornDiagnosisComponent::class
    ]
)
class AppActivitySubComponent
