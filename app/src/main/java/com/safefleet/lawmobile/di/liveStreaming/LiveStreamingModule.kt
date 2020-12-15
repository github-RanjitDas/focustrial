package com.safefleet.lawmobile.di.liveStreaming

import android.media.MediaActionSound
import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSourceImpl
import com.lawmobile.data.repository.liveStreaming.LiveStreamingRepositoryImpl
import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCaseImpl
import com.safefleet.mobile.external_hardware.cameras.CameraService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
class LiveStreamingModule {

    companion object {
        
        @Provides
        fun provideLiveRemoteDataSource(cameraService: CameraService): LiveStreamingRemoteDataSource =
            LiveStreamingRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideLiveRepository(liveStreamingRemoteDataSource: LiveStreamingRemoteDataSource): LiveStreamingRepository =
            LiveStreamingRepositoryImpl(liveStreamingRemoteDataSource)

        @Provides
        fun provideMediaActionSound() = MediaActionSound()

        @Provides
        fun provideLiveUseCase(liveRepository: LiveStreamingRepository): LiveStreamingUseCase =
            LiveStreamingUseCaseImpl(liveRepository)
    }
}