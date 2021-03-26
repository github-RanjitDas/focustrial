package com.safefleet.lawmobile.di.events

import android.os.Handler
import com.lawmobile.data.dao.CameraEventsDao
import com.lawmobile.data.datasource.local.events.EventsLocalDataSource
import com.lawmobile.data.datasource.local.events.EventsLocalDataSourceImpl
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSourceImpl
import com.lawmobile.data.repository.events.EventsRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.database.Database
import com.lawmobile.database.dao.CameraEventsDaoImpl
import com.lawmobile.domain.repository.events.EventsRepository
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.domain.usecase.events.EventsUseCaseImpl
import com.lawmobile.presentation.utils.CameraEventsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
class EventsModule {

    @Provides
    fun provideCameraEventsDao(database: Database): CameraEventsDao =
        CameraEventsDaoImpl(database)

    @Provides
    fun provideEventsLocalDataSource(eventsDao: CameraEventsDao): EventsLocalDataSource =
        EventsLocalDataSourceImpl(eventsDao)

    @Provides
    fun provideEventsRemoteDataSource(cameraService: CameraServiceFactory): EventsRemoteDataSource =
        EventsRemoteDataSourceImpl(cameraService)

    @Provides
    fun provideEventsRepository(eventsRemoteDataSource: EventsRemoteDataSource): EventsRepository =
        EventsRepositoryImpl(eventsRemoteDataSource)

    @Provides
    fun provideEventsUseCase(eventsRepository: EventsRepository): EventsUseCase =
        EventsUseCaseImpl(eventsRepository)

    @Provides
    fun provideCameraEventsManager(eventsUseCase: EventsUseCase): CameraEventsManager =
        CameraEventsManager(eventsUseCase, Dispatchers.IO, Handler())
}
