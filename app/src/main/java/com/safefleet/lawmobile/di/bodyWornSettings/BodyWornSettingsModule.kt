package com.safefleet.lawmobile.di.bodyWornSettings

import com.lawmobile.data.datasource.remote.bodyWornSettings.BodyWornSettingsDataSource
import com.lawmobile.data.datasource.remote.bodyWornSettings.BodyWornSettingsDataSourceImpl
import com.lawmobile.data.repository.bodyWornSettings.BodyWornSettingsRepositoryImpl
import com.lawmobile.domain.repository.bodyWornSettings.BodyWornSettingsRepository
import com.lawmobile.domain.usecase.bodyWornSettings.BodyWornSettingsUseCase
import com.lawmobile.domain.usecase.bodyWornSettings.BodyWornSettingsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class BodyWornSettingsModule {
    companion object {
        @Provides
        fun provideBodyWornSettingsDataSource(): BodyWornSettingsDataSource =
            BodyWornSettingsDataSourceImpl()

        @Provides
        fun provideBodyWornSettingsRepository(bodyWornSettingsDataSource: BodyWornSettingsDataSource): BodyWornSettingsRepository =
            BodyWornSettingsRepositoryImpl(bodyWornSettingsDataSource)

        @Provides
        fun provideBodyWornSettingsUseCase(bodyWornSettingsRepository: BodyWornSettingsRepository): BodyWornSettingsUseCase =
            BodyWornSettingsUseCaseImpl(bodyWornSettingsRepository)
    }
}
