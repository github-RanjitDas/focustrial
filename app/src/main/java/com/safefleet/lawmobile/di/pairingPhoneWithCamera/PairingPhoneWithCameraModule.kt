package com.safefleet.lawmobile.di.pairingPhoneWithCamera

import android.content.SharedPreferences
import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSource
import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSourceImpl
import com.lawmobile.data.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepositoryImpl
import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCaseImpl
import com.safefleet.mobile.avml.cameras.external.CameraDataSource
import dagger.Module
import dagger.Provides

@Module
class PairingPhoneWithCameraModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun providePairingPhoneWithCameraDataSource(
            preferences: SharedPreferences,
            cameraDataSource: CameraDataSource
        ): PairingPhoneWithCameraRemoteDataSource =
            PairingPhoneWithCameraRemoteDataSourceImpl(preferences, cameraDataSource)

        @JvmStatic
        @Provides
        fun providePairingPhoneWithCameraRepository(pairingPhoneWithCameraRemoteDataSource: PairingPhoneWithCameraRemoteDataSource): PairingPhoneWithCameraRepository =
            PairingPhoneWithCameraRepositoryImpl(pairingPhoneWithCameraRemoteDataSource)

        @JvmStatic
        @Provides
        fun providePairingPhoneWithCameraUseCase(pairingPhoneWithCameraRepository: PairingPhoneWithCameraRepository): PairingPhoneWithCameraUseCase =
            PairingPhoneWithCameraUseCaseImpl(pairingPhoneWithCameraRepository)
    }
}