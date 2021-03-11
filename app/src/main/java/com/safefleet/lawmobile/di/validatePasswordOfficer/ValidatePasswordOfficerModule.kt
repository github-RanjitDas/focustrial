package com.safefleet.lawmobile.di.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSource
import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSourceImpl
import com.lawmobile.data.repository.validatePasswordOfficer.ValidatePasswordOfficerRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.validatePasswordOfficer.ValidatePasswordOfficerRepository
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
class ValidatePasswordOfficerModule {

    companion object {

        @Provides
        fun provideValidatePasswordOfficerRemoteDataSource(
            cameraService: CameraServiceFactory
        ): ValidatePasswordOfficerRemoteDataSource =
            ValidatePasswordOfficerRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideValidatePasswordOfficerRepository(
            validatePasswordOfficerRemoteDataSource: ValidatePasswordOfficerRemoteDataSource
        ): ValidatePasswordOfficerRepository =
            ValidatePasswordOfficerRepositoryImpl(
                validatePasswordOfficerRemoteDataSource
            )

        @Provides
        fun provideValidatePasswordOfficerUseCase(validatePasswordOfficerRepository: ValidatePasswordOfficerRepository): ValidatePasswordOfficerUseCase =
            ValidatePasswordOfficerUseCaseImpl(validatePasswordOfficerRepository)
    }
}
