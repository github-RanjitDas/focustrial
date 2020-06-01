package com.safefleet.lawmobile.di.cameraService

import android.content.SharedPreferences
import com.google.gson.Gson
import com.lawmobile.domain.CameraInfo
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.avml.cameras.external.*
import com.safefleet.mobile.avml.cameras.external.x1.CameraConnectServiceX1
import com.safefleet.mobile.avml.cameras.external.x1.CameraHelperX1
import dagger.Module
import dagger.Provides
import java.net.Socket
import javax.inject.Singleton

@Module
class CameraServiceModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @Singleton
        fun provideCameraPreferences(sharedPreferences: SharedPreferences): CameraPreferences =
            CameraPreferences(sharedPreferences)

        @JvmStatic
        @Provides
        @Singleton
        fun provideCameraHelperX1(): CameraHelperX1 =
            CameraHelperX1(Gson(), Socket(), Socket())

        @JvmStatic
        @Provides
        @Singleton
        fun provideCameraService(
            cameraHelperX1: CameraHelperX1,
            cameraPreferences: CameraPreferences
        ): CameraConnectService {
            return when (CameraInfo.cameraType) {
                CameraType.X1 -> CameraConnectServiceX1(cameraHelperX1, cameraPreferences)
            }
        }

        @JvmStatic
        @Provides
        @Singleton
        fun provideCameraHelper(
            cameraConnectService: CameraConnectService,
            wifiHelper: WifiHelper
        ): CameraHelper = CameraHelper(cameraConnectService, wifiHelper)
    }
}