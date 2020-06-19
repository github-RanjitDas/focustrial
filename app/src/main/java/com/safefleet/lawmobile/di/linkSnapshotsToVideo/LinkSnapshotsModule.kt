package com.safefleet.lawmobile.di.linkSnapshotsToVideo

import com.lawmobile.data.datasource.remote.linkSnapshotsToVideo.LinkSnapshotsRemoteDataSource
import com.lawmobile.data.datasource.remote.linkSnapshotsToVideo.LinkSnapshotsRemoteDataSourceImpl
import com.lawmobile.data.repository.linkSnapshotsToVideo.LinkSnapshotsRepositoryImpl
import com.lawmobile.domain.repository.linkSnapshotsToVideo.LinkSnapshotsRepository
import com.lawmobile.domain.usecase.linkSnapshotsToVideo.LinkSnapshotsUseCase
import com.lawmobile.domain.usecase.linkSnapshotsToVideo.LinkSnapshotsUseCaseImpl
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import dagger.Module
import dagger.Provides

@Module
class LinkSnapshotsModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideLinkSnapshotsRemoteDataSource(cameraConnectService: CameraConnectService): LinkSnapshotsRemoteDataSource =
            LinkSnapshotsRemoteDataSourceImpl(cameraConnectService)

        @JvmStatic
        @Provides
        fun provideLinkSnapshotsRepository(linkSnapshotsRemoteDataSource: LinkSnapshotsRemoteDataSource): LinkSnapshotsRepository =
            LinkSnapshotsRepositoryImpl(linkSnapshotsRemoteDataSource)

        @JvmStatic
        @Provides
        fun provideLinkSnapshotsUseCase(linkSnapshotsRepository: LinkSnapshotsRepository): LinkSnapshotsUseCase =
            LinkSnapshotsUseCaseImpl(linkSnapshotsRepository)
    }
}