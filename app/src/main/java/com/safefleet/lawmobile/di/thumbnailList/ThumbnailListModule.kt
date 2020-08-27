package com.safefleet.lawmobile.di.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSourceImpl
import com.lawmobile.data.repository.thumbnailList.ThumbnailListRepositoryImpl
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCaseImpl
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
class ThumbnailListModule {

    companion object {
        @Provides
        fun provideThumbnailListRemoteDataSource(cameraConnectService: CameraConnectService): ThumbnailListRemoteDataSource =
            ThumbnailListRemoteDataSourceImpl(cameraConnectService)

        @Provides
        fun provideThumbnailListRepository(thumbnailListRemoteDataSource: ThumbnailListRemoteDataSource): ThumbnailListRepository =
            ThumbnailListRepositoryImpl(thumbnailListRemoteDataSource)

        @Provides
        fun provideThumbnailListUseCase(thumbnailListRepository: ThumbnailListRepository): ThumbnailListUseCase =
            ThumbnailListUseCaseImpl(thumbnailListRepository)
    }
}