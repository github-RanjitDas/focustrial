package com.safefleet.lawmobile.di.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSource
import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSourceImpl
import com.lawmobile.data.repository.validatePasswordOfficer.ValidatePasswordOfficerRepositoryImpl
import com.lawmobile.domain.repository.validatePasswordOfficer.ValidatePasswordOfficerRepository
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCaseImpl
import com.safefleet.mobile.avml.cameras.CameraDataSource
import dagger.Module
import dagger.Provides

@Module
class ValidatePasswordOfficerModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideValidatePasswordOfficerRemoteDataSource(
            cameraDataSource: CameraDataSource
        ): ValidatePasswordOfficerRemoteDataSource =
            ValidatePasswordOfficerRemoteDataSourceImpl(cameraDataSource)

        @JvmStatic
        @Provides
        fun provideValidatePasswordOfficerRepository(
            validatePasswordOfficerRemoteDataSource: ValidatePasswordOfficerRemoteDataSource
        ): ValidatePasswordOfficerRepository =
            ValidatePasswordOfficerRepositoryImpl(
                validatePasswordOfficerRemoteDataSource
            )

        @JvmStatic
        @Provides
        fun provideValidatePasswordOfficerUseCase(validatePasswordOfficerRepository: ValidatePasswordOfficerRepository): ValidatePasswordOfficerUseCase =
            ValidatePasswordOfficerUseCaseImpl(validatePasswordOfficerRepository)


    }
}