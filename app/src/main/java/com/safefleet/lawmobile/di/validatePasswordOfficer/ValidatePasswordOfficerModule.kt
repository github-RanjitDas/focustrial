package com.safefleet.lawmobile.di.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validateOfficerPassword.ValidateOfficerPasswordRemoteDataSource
import com.lawmobile.data.datasource.remote.validateOfficerPassword.ValidateOfficerPasswordRemoteDataSourceImpl
import com.lawmobile.data.repository.validateOfficerPassword.ValidateOfficerPasswordRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.validateOfficerPassword.ValidateOfficerPasswordRepository
import com.lawmobile.domain.usecase.validateOfficerPassword.ValidateOfficerPasswordUseCase
import com.lawmobile.domain.usecase.validateOfficerPassword.ValidateOfficerPasswordUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class ValidatePasswordOfficerModule {

    companion object {

        @Provides
        fun provideValidatePasswordOfficerRemoteDataSource(
            cameraService: CameraServiceFactory
        ): ValidateOfficerPasswordRemoteDataSource =
            ValidateOfficerPasswordRemoteDataSourceImpl(cameraService)

        @Provides
        fun provideValidatePasswordOfficerRepository(
            validateOfficerPasswordRemoteDataSource: ValidateOfficerPasswordRemoteDataSource
        ): ValidateOfficerPasswordRepository =
            ValidateOfficerPasswordRepositoryImpl(validateOfficerPasswordRemoteDataSource)

        @Provides
        fun provideValidatePasswordOfficerUseCase(
            validateOfficerPasswordRepository: ValidateOfficerPasswordRepository
        ): ValidateOfficerPasswordUseCase =
            ValidateOfficerPasswordUseCaseImpl(validateOfficerPasswordRepository)
    }
}
