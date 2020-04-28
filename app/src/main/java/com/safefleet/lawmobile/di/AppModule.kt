@file:Suppress("DEPRECATION")

package com.safefleet.lawmobile.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import com.google.gson.Gson
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.lawmobile.presentation.utils.WifiConnection
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.lawmobile.BaseApplication
import com.safefleet.mobile.avml.cameras.CameraDataSource
import com.safefleet.mobile.avml.cameras.CameraDataSourceImpl
import com.safefleet.mobile.avml.cameras.CameraType
import dagger.Module
import dagger.Provides
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import javax.inject.Singleton

@Module
class AppModule {

    @Module
    companion object {

        private const val PREF_NAME = "authentication"

        @JvmStatic
        @Provides
        fun provideApplication(application: BaseApplication): Application = application

        @JvmStatic
        @Provides
        @Singleton
        fun provideWifiManager(application: BaseApplication): WifiManager =
            application.applicationContext.getSystemService(
                Context.WIFI_SERVICE
            ) as WifiManager

        @JvmStatic
        @Provides
        @Singleton
        fun providePreferences(application: Application): SharedPreferences =
            application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        @JvmStatic
        @Provides
        @Singleton
        fun provideConnectivityManager(application: BaseApplication): ConnectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @JvmStatic
        @Provides
        @Singleton
        fun provideWifiHelper(wifiManager: WifiManager): WifiHelper =
            WifiHelper(wifiManager)

        @JvmStatic
        @Provides
        @Singleton
        fun provideWifiConfiguration(): WifiConfiguration = WifiConfiguration()

        @Suppress("DEPRECATION")
        @JvmStatic
        @Provides
        @Singleton
        fun provideWifiConnection(
            wifiManager: WifiManager,
            wifiConfiguration: WifiConfiguration,
            connectivityManager: ConnectivityManager
        ): WifiConnection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                WifiConnection(
                    wifiManager,
                    wifiConfiguration,
                    connectivityManager,
                    WifiNetworkSpecifier.Builder(),
                    NetworkRequest.Builder()
                )
            } else {
                WifiConnection(
                    wifiManager,
                    wifiConfiguration,
                    connectivityManager,
                    null,
                    NetworkRequest.Builder()
                )
            }

        @JvmStatic
        @Provides
        @Singleton
        fun provideCameraDataSource(preferences: SharedPreferences): CameraDataSource =
            CameraDataSourceImpl(CameraType.X1, preferences)

        @JvmStatic
        @Provides
        @Singleton
        fun provideCameraHelper(
            cameraDataSource: CameraDataSource,
            wifiHelper: WifiHelper
        ): CameraHelper = CameraHelper(cameraDataSource, wifiHelper)

        @JvmStatic
        @Provides
        @Singleton
        fun provideGSON() = Gson()

        @JvmStatic
        @Provides
        @Singleton
        fun provideLibVLC(application: BaseApplication): LibVLC = LibVLC(application)

        @JvmStatic
        @Provides
        @Singleton
        fun provideMediaPlayer(libVLC: LibVLC): MediaPlayer = MediaPlayer(libVLC)

        @JvmStatic
        @Provides
        @Singleton
        fun provideVLCMediaPlayer(libVLC: LibVLC, mediaPlayer: MediaPlayer): VLCMediaPlayer =
            VLCMediaPlayer(libVLC, mediaPlayer)

    }
}