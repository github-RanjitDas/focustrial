package com.safefleet.lawmobile.di.typeOfCamera

import com.lawmobile.data.datasource.remote.typeOfCamera.TypeOfCameraRemoteDataSource
import com.lawmobile.data.datasource.remote.typeOfCamera.TypeOfCameraRemoteDataSourceImpl
import com.lawmobile.data.repository.typeOfCamera.TypeOfCameraRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.typeOfCamera.TypeOfCameraRepository
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCase
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class TypeOfCameraModule {
    companion object {
        @Provides
        fun provideDataSource(cameraServiceFactory: CameraServiceFactory): TypeOfCameraRemoteDataSource =
            TypeOfCameraRemoteDataSourceImpl(cameraServiceFactory)

        @Provides
        fun provideRepository(typeOfCameraRemoteDataSource: TypeOfCameraRemoteDataSource): TypeOfCameraRepository =
            TypeOfCameraRepositoryImpl(typeOfCameraRemoteDataSource)

        @Provides
        fun provideUseCase(typeOfCameraRepository: TypeOfCameraRepository): TypeOfCameraUseCase =
            TypeOfCameraUseCaseImpl(typeOfCameraRepository)
    }
}
