package com.safefleet.lawmobile.di.cameraService

import com.google.gson.Gson
import com.lawmobile.data.utils.ConnectionHelperImpl
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.CameraType
import com.lawmobile.domain.utils.ConnectionHelper
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceX1Mock
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.x1.X1CameraHelper
import com.safefleet.mobile.kotlin_commons.socket.SocketHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import java.net.Socket
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class CameraServiceModule {

    companion object {

        @Provides
        fun provideSocketHelper(): SocketHelper = SocketHelper(Socket())

        @Provides
        @Singleton
        fun provideCameraHelperX1(cmdHelper: SocketHelper, dataHelper: SocketHelper): X1CameraHelper =
            X1CameraHelper(Gson(), cmdHelper, dataHelper)

        @Provides
        @Singleton
        fun provideCameraService(x1CameraHelper: X1CameraHelper): CameraService {
            return when (CameraInfo.cameraType) {
                CameraType.X1 -> CameraConnectServiceX1Mock()
            }
        }

        @Provides
        @Singleton
        fun provideConnectionHelper(cameraService: CameraService): ConnectionHelper =
            ConnectionHelperImpl(cameraService)

        @Provides
        @Singleton
        fun provideCameraHelper(
            connectionHelper: ConnectionHelper,
            wifiHelper: WifiHelper
        ): CameraHelper = CameraHelper(connectionHelper, wifiHelper)
    }
}
