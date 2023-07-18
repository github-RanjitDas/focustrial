package net.safefleet.focus.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.safefleet.focus.di.audioDetail.AudioDetailComponent
import net.safefleet.focus.di.bodyWornDiagnosis.BodyWornDiagnosisComponent
import net.safefleet.focus.di.bodyWornSettings.BodyWornSettingsComponent
import net.safefleet.focus.di.fileList.FileListComponent
import net.safefleet.focus.di.helpSection.HelpPageComponent
import net.safefleet.focus.di.liveStreaming.LiveStreamingComponent
import net.safefleet.focus.di.login.LoginComponent
import net.safefleet.focus.di.snapshotDetail.SnapshotDetailComponent
import net.safefleet.focus.di.typeOfCamera.TypeOfCameraComponent
import net.safefleet.focus.di.videoPlayback.VideoPlaybackComponent

@InstallIn(SingletonComponent::class)
@Module(
    subcomponents = [
        FileListComponent::class,
        HelpPageComponent::class,
        LiveStreamingComponent::class,
        LoginComponent::class,
        SnapshotDetailComponent::class,
        AudioDetailComponent::class,
        VideoPlaybackComponent::class,
        BodyWornDiagnosisComponent::class,
        BodyWornSettingsComponent::class,
        TypeOfCameraComponent::class
    ]
)
class AppActivitySubComponent
