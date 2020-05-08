package com.safefleet.lawmobile.di.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSourceImpl
import com.lawmobile.data.repository.videoPlayback.VideoPlaybackRepositoryImpl
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCaseImpl
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import dagger.Module
import dagger.Provides

@Module
class VideoPlaybackModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideVideoPlaybackRemoteDataSource(cameraConnectService: CameraConnectService): VideoPlaybackRemoteDataSource =
            VideoPlaybackRemoteDataSourceImpl(cameraConnectService)

        @JvmStatic
        @Provides
        fun provideVideoPlaybackRepository(videoPlaybackRemoteDataSource: VideoPlaybackRemoteDataSource): VideoPlaybackRepository =
            VideoPlaybackRepositoryImpl(videoPlaybackRemoteDataSource)

        @JvmStatic
        @Provides
        fun provideVideoPlaybackUseCase(videoPlaybackRepository: VideoPlaybackRepository): VideoPlaybackUseCase =
            VideoPlaybackUseCaseImpl(videoPlaybackRepository)

    }
}