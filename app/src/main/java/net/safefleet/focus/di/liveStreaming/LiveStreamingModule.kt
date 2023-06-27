package net.safefleet.focus.di.liveStreaming

import android.media.MediaActionSound
import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSourceImpl
import com.lawmobile.data.repository.liveStreaming.LiveStreamingRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class LiveStreamingModule {

    companion object {

        @Provides
        fun provideLiveRemoteDataSource(cameraService: CameraServiceFactory): LiveStreamingRemoteDataSource =
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
