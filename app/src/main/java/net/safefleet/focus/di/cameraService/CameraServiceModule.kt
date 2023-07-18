package net.safefleet.focus.di.cameraService

import com.google.gson.Gson
import com.lawmobile.body_cameras.CameraService
import com.lawmobile.body_cameras.socket.SocketHelper
import com.lawmobile.body_cameras.utils.CommandHelper
import com.lawmobile.body_cameras.utils.FileInformationHelper
import com.lawmobile.body_cameras.utils.MetadataHelper
import com.lawmobile.body_cameras.utils.NotificationCameraHelper
import com.lawmobile.body_cameras.x2.X2CameraServiceImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.data.utils.CameraServiceFactoryImpl
import com.lawmobile.data.utils.ConnectionHelperImpl
import com.lawmobile.domain.utils.ConnectionHelper
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.utils.CameraHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
        @Named("x2CameraService")
        fun provideCameraServiceX2(
            notificationCameraHelper: NotificationCameraHelper,
            fileInformationHelper: FileInformationHelper,
            commandHelper: CommandHelper,
            metadataHelper: MetadataHelper
        ): CameraService {
            return X2CameraServiceImpl(
                notificationCameraHelper,
                fileInformationHelper,
                commandHelper,
                metadataHelper
            )
        }

        @Provides
        @Singleton
        fun provideCameraServiceFactory(
            @Named("x2CameraService") x2CameraService: CameraService
        ): CameraServiceFactory {
            return CameraServiceFactoryImpl(x2CameraService)
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
