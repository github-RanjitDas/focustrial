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
import com.safefleet.mobile.external_hardware.cameras.utils.CommandHelper
import com.safefleet.mobile.external_hardware.cameras.utils.FileInformationHelper
import com.safefleet.mobile.external_hardware.cameras.utils.MetadataHelper
import com.safefleet.mobile.external_hardware.cameras.utils.NotificationCameraHelper
import com.safefleet.mobile.kotlin_commons.socket.SocketHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
        fun provideCommandHelper(
            socketHelper: SocketHelper
        ): CommandHelper = CommandHelper(Gson(), socketHelper)

        @Provides
        @Singleton
        fun provideFileInformationHelper(
            commandHelper: CommandHelper,
            dataSocketHelper: SocketHelper
        ): FileInformationHelper = FileInformationHelper(Gson(), commandHelper, dataSocketHelper)

        @Provides
        @Singleton
        fun provideMetadataHelper(
            fileInformationHelper: FileInformationHelper,
            commandHelper: CommandHelper
        ): MetadataHelper = MetadataHelper(Gson(), fileInformationHelper, commandHelper)

        @Provides
        @Singleton
        fun provideNotificationHelper(
            cmdHelper: CommandHelper
        ): NotificationCameraHelper = NotificationCameraHelper(cmdHelper)

        @Provides
        @Singleton
        @Named("x1CameraService")
        fun provideCameraServiceX1(
            fileInformationHelper: FileInformationHelper,
            commandHelper: CommandHelper,
            metadataHelper: MetadataHelper
        ): CameraService {
            return MockUtils.cameraConnectServiceX1Mock
        }

        @Provides
        @Singleton
        @Named("x2CameraService")
        fun provideCameraServiceX2(
            notificationCameraHelper: NotificationCameraHelper,
            fileInformationHelper: FileInformationHelper,
            commandHelper: CommandHelper,
            metadataHelper: MetadataHelper
        ): CameraService {
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
            wifiHelper: WifiHelper,
            dispatcher: CoroutineDispatcher
        ): CameraHelper = CameraHelper(connectionHelper, wifiHelper, dispatcher)
    }
}
