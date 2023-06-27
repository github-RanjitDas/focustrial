package net.safefleet.focus.di.audioDetail

import com.lawmobile.data.datasource.remote.audioDetail.AudioDetailRemoteDataSource
import com.lawmobile.data.datasource.remote.audioDetail.AudioDetailRemoteDataSourceImpl
import com.lawmobile.data.repository.audioDetail.AudioDetailRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.audioDetail.AudioDetailRepository
import com.lawmobile.domain.usecase.audioDetail.AudioDetailUseCase
import com.lawmobile.domain.usecase.audioDetail.AudioDetailUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AudioDetailModule {

    companion object {
        @Provides
        fun provideSnapshotDetailRemoteDataSource(cameraService: CameraServiceFactory): AudioDetailRemoteDataSource =
            AudioDetailRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideSnapshotDetailRepository(snapshotDetailRemoteDataSource: AudioDetailRemoteDataSource): AudioDetailRepository =
            AudioDetailRepositoryImpl(snapshotDetailRemoteDataSource)

        @Provides
        fun provideSnapshotDetailUseCase(snapshotDetailRepository: AudioDetailRepository): AudioDetailUseCase =
            AudioDetailUseCaseImpl(snapshotDetailRepository)
    }
}
