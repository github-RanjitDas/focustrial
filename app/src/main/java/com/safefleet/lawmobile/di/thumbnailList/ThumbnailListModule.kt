package com.safefleet.lawmobile.di.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSourceImpl
import com.lawmobile.data.repository.thumbnailList.ThumbnailListRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class ThumbnailListModule {

    companion object {
        @Provides
        fun provideThumbnailListRemoteDataSource(cameraService: CameraServiceFactory): ThumbnailListRemoteDataSource =
            ThumbnailListRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideThumbnailListRepository(thumbnailListRemoteDataSource: ThumbnailListRemoteDataSource): ThumbnailListRepository =
            ThumbnailListRepositoryImpl(thumbnailListRemoteDataSource)

        @Provides
        fun provideThumbnailListUseCase(thumbnailListRepository: ThumbnailListRepository): ThumbnailListUseCase =
            ThumbnailListUseCaseImpl(thumbnailListRepository)
    }
}
