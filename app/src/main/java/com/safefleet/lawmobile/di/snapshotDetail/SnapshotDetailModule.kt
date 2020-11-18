package com.safefleet.lawmobile.di.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSourceImpl
import com.lawmobile.data.repository.snapshotDetail.SnapshotDetailRepositoryImpl
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCaseImpl
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import dagger.Module
import dagger.Provides

@Module
class SnapshotDetailModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideSnapshotDetailRemoteDataSource(cameraConnectService: CameraConnectService): SnapshotDetailRemoteDataSource =
            SnapshotDetailRemoteDataSourceImpl(cameraConnectService)

        @JvmStatic
        @Provides
        fun provideSnapshotDetailRepository(snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource): SnapshotDetailRepository =
            SnapshotDetailRepositoryImpl(snapshotDetailRemoteDataSource)

        @JvmStatic
        @Provides
        fun provideSnapshotDetailUseCase(snapshotDetailRepository: SnapshotDetailRepository): SnapshotDetailUseCase =
            SnapshotDetailUseCaseImpl(snapshotDetailRepository)

    }
}