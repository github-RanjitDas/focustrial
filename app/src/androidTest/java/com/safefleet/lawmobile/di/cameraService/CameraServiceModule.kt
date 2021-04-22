package com.safefleet.lawmobile.di.cameraService

import com.google.gson.Gson
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.data.utils.CameraServiceFactoryImpl
import com.lawmobile.data.utils.ConnectionHelperImpl
import com.lawmobile.domain.utils.ConnectionHelper
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.lawmobile.helpers.MockUtils
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.helpers.XCameraHelper
import com.safefleet.mobile.kotlin_commons.socket.SocketHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.net.Socket
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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
            return MockUtils.cameraConnectServiceX1Mock
        }

        @Provides
        @Singleton
        @Named("x2CameraService")
        fun provideCameraServiceX2(xCameraHelper: XCameraHelper): CameraService {
            return MockUtils.cameraConnectServiceX1Mock
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
    }
}
