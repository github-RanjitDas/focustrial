package com.safefleet.lawmobile.di.pairingPhoneWithCamera

import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSource
import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSourceImpl
import com.lawmobile.data.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class PairingPhoneWithCameraModule {

    companion object {

        @Provides
        fun providePairingPhoneWithCameraDataSource(
            cameraService: CameraServiceFactory
        ): PairingPhoneWithCameraRemoteDataSource =
            PairingPhoneWithCameraRemoteDataSourceImpl(cameraService)

        @Provides
        fun providePairingPhoneWithCameraRepository(pairingPhoneWithCameraRemoteDataSource: PairingPhoneWithCameraRemoteDataSource): PairingPhoneWithCameraRepository =
            PairingPhoneWithCameraRepositoryImpl(pairingPhoneWithCameraRemoteDataSource)

        @Provides
        fun providePairingPhoneWithCameraUseCase(pairingPhoneWithCameraRepository: PairingPhoneWithCameraRepository): PairingPhoneWithCameraUseCase =
            PairingPhoneWithCameraUseCaseImpl(pairingPhoneWithCameraRepository)
    }
}
