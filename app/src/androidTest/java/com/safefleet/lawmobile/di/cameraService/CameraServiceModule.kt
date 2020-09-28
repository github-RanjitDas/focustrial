package com.safefleet.lawmobile.di.cameraService

import com.google.gson.Gson
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceX1Mock
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.avml.cameras.external.CameraType
import com.safefleet.mobile.avml.cameras.external.socket.SocketHelper
import com.safefleet.mobile.avml.cameras.external.x1.CameraHelperX1
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
        fun provideCameraHelperX1(cmdHelper: SocketHelper, dataHelper: SocketHelper): CameraHelperX1 =
            CameraHelperX1(Gson(), cmdHelper, dataHelper)

        @Provides
        @Singleton
        fun provideCameraService(cameraHelperX1: CameraHelperX1): CameraConnectService {
            return when (CameraInfo.cameraType) {
                CameraType.X1 -> CameraConnectServiceX1Mock()
            }
        }

        @Provides
        @Singleton
        fun provideCameraHelper(
            cameraConnectService: CameraConnectService,
            wifiHelper: WifiHelper
        ): CameraHelper = CameraHelper(cameraConnectService, wifiHelper)
    }
}
