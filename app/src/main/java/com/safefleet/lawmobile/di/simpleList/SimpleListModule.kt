package com.safefleet.lawmobile.di.simpleList

import com.lawmobile.data.datasource.remote.simpleList.SimpleListRemoteDataSource
import com.lawmobile.data.datasource.remote.simpleList.SimpleListRemoteDataSourceImpl
import com.lawmobile.data.repository.simpleList.SimpleListRepositoryImpl
import com.lawmobile.domain.repository.simpleList.SimpleListRepository
import com.lawmobile.domain.usecase.simpleList.SimpleListUseCase
import com.lawmobile.domain.usecase.simpleList.SimpleListUseCaseImpl
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
class SimpleListModule {
    companion object {

        @Provides
        fun provideSimpleListRemoteDataSource(cameraConnectService: CameraConnectService): SimpleListRemoteDataSource =
            SimpleListRemoteDataSourceImpl(cameraConnectService)

        @Provides
        fun provideSimpleListRepository(simpleListRemoteDataSource: SimpleListRemoteDataSource): SimpleListRepository =
            SimpleListRepositoryImpl(simpleListRemoteDataSource)

        @Provides
        fun provideSimpleListUseCase(simpleListRepository: SimpleListRepository): SimpleListUseCase =
            SimpleListUseCaseImpl(simpleListRepository)
    }
}