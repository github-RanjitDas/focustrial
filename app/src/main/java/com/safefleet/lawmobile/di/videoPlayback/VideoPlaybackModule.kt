package com.safefleet.lawmobile.di.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSourceImpl
import com.lawmobile.data.repository.videoPlayback.VideoPlaybackRepositoryImpl
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCaseImpl
import com.safefleet.mobile.external_hardware.cameras.CameraService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class VideoPlaybackModule {
    
    companion object {
        
        @Provides
        fun provideVideoPlaybackRemoteDataSource(cameraService: CameraService): VideoPlaybackRemoteDataSource =
            VideoPlaybackRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideVideoPlaybackRepository(videoPlaybackRemoteDataSource: VideoPlaybackRemoteDataSource): VideoPlaybackRepository =
            VideoPlaybackRepositoryImpl(videoPlaybackRemoteDataSource)

        @Provides
        fun provideVideoPlaybackUseCase(videoPlaybackRepository: VideoPlaybackRepository): VideoPlaybackUseCase =
            VideoPlaybackUseCaseImpl(videoPlaybackRepository)

    }
}