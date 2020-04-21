package com.safefleet.lawmobile.di.liveStreaming

import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSourceImpl
import com.lawmobile.data.repository.liveStreaming.LiveStreamingRepositoryImpl
import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCaseImpl
import com.safefleet.mobile.avml.cameras.CameraDataSource
import dagger.Module
import dagger.Provides

@Module
class LiveStreamingModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideLiveRemoteDataSource(cameraDataSource: CameraDataSource): LiveStreamingRemoteDataSource =
            LiveStreamingRemoteDataSourceImpl(cameraDataSource)

        @JvmStatic
        @Provides
        fun provideLiveRepository(liveStreamingRemoteDataSource: LiveStreamingRemoteDataSource): LiveStreamingRepository =
            LiveStreamingRepositoryImpl(liveStreamingRemoteDataSource)

        @JvmStatic
        @Provides
        fun provideLiveUseCase(liveRepository: LiveStreamingRepository): LiveStreamingUseCase =
            LiveStreamingUseCaseImpl(liveRepository)
    }
}