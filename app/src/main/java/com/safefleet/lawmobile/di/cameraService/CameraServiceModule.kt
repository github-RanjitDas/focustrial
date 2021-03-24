package com.safefleet.lawmobile.di.cameraService

import android.os.Handler
import com.google.gson.Gson
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSourceImpl
import com.lawmobile.data.repository.events.EventsRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.data.utils.CameraServiceFactoryImpl
import com.lawmobile.data.utils.ConnectionHelperImpl
import com.lawmobile.domain.repository.events.EventsRepository
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.domain.usecase.events.EventsUseCaseImpl
import com.lawmobile.domain.utils.ConnectionHelper
import com.lawmobile.presentation.utils.CameraEventsManager
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.helpers.XCameraHelper
import com.safefleet.mobile.external_hardware.cameras.x1.X1CameraServiceImpl
import com.safefleet.mobile.external_hardware.cameras.x2.X2CameraServiceImpl
import com.safefleet.mobile.kotlin_commons.socket.SocketHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import java.net.Socket
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class CameraServiceModule {
    companion object {
        @Provides
        fun provideSocketHelper(): SocketHelper = SocketHelper(Socket())

        @Provides
        @Singleton
        fun provideCameraHelperX1(
            cmdHelper: SocketHelper,
            dataHelper: SocketHelper
        ): XCameraHelper =
            XCameraHelper(Gson(), cmdHelper, dataHelper)

        @Provides
        @Singleton
        @Named("x1CameraService")
        fun provideCameraServiceX1(xCameraHelper: XCameraHelper): CameraService {
            return X1CameraServiceImpl(xCameraHelper)
        }

        @Provides
        @Singleton
        @Named("x2CameraService")
        fun provideCameraServiceX2(xCameraHelper: XCameraHelper): CameraService {
            return X2CameraServiceImpl(xCameraHelper)
        }

        @Provides
        @Singleton
        fun provideCameraServiceFactory(
            @Named("x1CameraService") x1CameraService: CameraService,
            @Named("x2CameraService") x2CameraService: CameraService
        ): CameraServiceFactory {
            return CameraServiceFactoryImpl(x1CameraService, x2CameraService)
        }

        @Provides
        @Singleton
        fun provideConnectionHelper(cameraServiceFactory: CameraServiceFactory): ConnectionHelper =
            ConnectionHelperImpl(cameraServiceFactory)

        @Provides
        @Singleton
        fun provideCameraHelper(
            connectionHelper: ConnectionHelper,
            wifiHelper: WifiHelper
        ): CameraHelper = CameraHelper(connectionHelper, wifiHelper)

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
}
