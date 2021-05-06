package com.safefleet.lawmobile.di.bodyWornDiagnosis

import com.lawmobile.data.datasource.remote.bodyWornDiagnosis.BodyWornDiagnosisDataSource
import com.lawmobile.data.datasource.remote.bodyWornDiagnosis.BodyWornDiagnosisDataSourceImpl
import com.lawmobile.data.repository.bodyWornDiagnosis.BodyWornDiagnosisRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.bodyWornDiagnosis.BodyWornDiagnosisRepository
import com.lawmobile.domain.usecase.bodyWornDiagnosis.BodyWornDiagnosisUseCase
import com.lawmobile.domain.usecase.bodyWornDiagnosis.BodyWornDiagnosisUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class BodyWornDiagnosisModule {

    companion object {

        @Provides
        fun provideBodyWornDiagnosisDataSource(cameraFactory: CameraServiceFactory): BodyWornDiagnosisDataSource =
            BodyWornDiagnosisDataSourceImpl(cameraFactory)

        @Provides
        fun provideBodyWornDiagnosisRepository(bodyWornDiagnosisDataSource: BodyWornDiagnosisDataSource): BodyWornDiagnosisRepository =
            BodyWornDiagnosisRepositoryImpl(bodyWornDiagnosisDataSource)

        @Provides
        fun provideBodyWornDiagnosisUseCase(bodyWornDiagnosisRepository: BodyWornDiagnosisRepository): BodyWornDiagnosisUseCase =
            BodyWornDiagnosisUseCaseImpl(bodyWornDiagnosisRepository)
    }
}
