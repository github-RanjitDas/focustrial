package com.safefleet.lawmobile.di.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSourceImpl
import com.lawmobile.data.repository.snapshotDetail.SnapshotDetailRepositoryImpl
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCaseImpl
import com.safefleet.mobile.external_hardware.cameras.CameraService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
class SnapshotDetailModule {

    companion object {
        @Provides
        fun provideSnapshotDetailRemoteDataSource(cameraService: CameraService): SnapshotDetailRemoteDataSource =
            SnapshotDetailRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideSnapshotDetailRepository(snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource): SnapshotDetailRepository =
            SnapshotDetailRepositoryImpl(snapshotDetailRemoteDataSource)

        @Provides
        fun provideSnapshotDetailUseCase(snapshotDetailRepository: SnapshotDetailRepository): SnapshotDetailUseCase =
            SnapshotDetailUseCaseImpl(snapshotDetailRepository)
    }
}
