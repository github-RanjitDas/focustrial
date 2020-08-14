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
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
class LinkSnapshotsModule {

    companion object {
        @Provides
        fun provideLinkSnapshotsRemoteDataSource(cameraConnectService: CameraConnectService): LinkSnapshotsRemoteDataSource =
            LinkSnapshotsRemoteDataSourceImpl(cameraConnectService)

        @Provides
        fun provideLinkSnapshotsRepository(linkSnapshotsRemoteDataSource: LinkSnapshotsRemoteDataSource): LinkSnapshotsRepository =
            LinkSnapshotsRepositoryImpl(linkSnapshotsRemoteDataSource)

        @Provides
        fun provideLinkSnapshotsUseCase(linkSnapshotsRepository: LinkSnapshotsRepository): LinkSnapshotsUseCase =
            LinkSnapshotsUseCaseImpl(linkSnapshotsRepository)
    }
}