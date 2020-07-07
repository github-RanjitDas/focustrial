@file:Suppress("DEPRECATION")

package com.safefleet.lawmobile.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import com.google.gson.Gson
import com.lawmobile.presentation.utils.MobileDataStatus
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.lawmobile.BaseApplication
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

        @JvmStatic
        @Provides
        @Singleton
        fun provideMobileDataStatus(connectivityManager: ConnectivityManager) =
            MobileDataStatus(connectivityManager)

    }
}