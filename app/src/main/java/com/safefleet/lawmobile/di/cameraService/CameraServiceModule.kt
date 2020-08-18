package com.safefleet.lawmobile.di.cameraService

import android.content.SharedPreferences
import com.google.gson.Gson
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.avml.cameras.external.*
import com.safefleet.mobile.avml.cameras.external.x1.CameraConnectServiceX1
import com.safefleet.mobile.avml.cameras.external.x1.CameraHelperX1
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import java.net.Socket
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class CameraServiceModule {

    companion object {

        @Provides
        @Singleton
        fun provideCameraPreferences(sharedPreferences: SharedPreferences): CameraPreferences =
            CameraPreferences(sharedPreferences)

        @Provides
        @Singleton
        fun provideCameraHelperX1(): CameraHelperX1 =
            CameraHelperX1(Gson(), Socket(), Socket())

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

        @Provides
        @Singleton
        fun provideCameraHelper(
            cameraConnectService: CameraConnectService,
            wifiHelper: WifiHelper
        ): CameraHelper = CameraHelper(cameraConnectService, wifiHelper)
    }
}