@file:Suppress("DEPRECATION")

package com.safefleet.lawmobile.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import com.google.gson.Gson
import com.lawmobile.presentation.utils.MobileDataStatus
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.lawmobile.testData.TestLoginData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mockk.every
import io.mockk.mockk
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    companion object {
        var wifiEnabled = true

        @Provides
        @Singleton
        fun provideWifiManager(@ApplicationContext context: Context): WifiManager =
            context.applicationContext.getSystemService(
                Context.WIFI_SERVICE
            ) as WifiManager

        @Provides
        @Singleton
        fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @Provides
        fun provideWifiHelper(wifiManager: WifiManager): WifiHelper = mockk {
            every { getGatewayAddress() } returns "192.168.42.1"
            every { getIpAddress() } returns "192.168.42.2"
            every { isEqualsValueWithSSID(TestLoginData.SSID.value) } returns true
            every { isEqualsValueWithSSID(TestLoginData.INVALID_SSID.value) } returns false
            if (wifiEnabled) {
                every { isWifiEnable() } returns true
            } else {
                every { isWifiEnable() } returns false andThen true
            }
            every { getSSIDWiFi() } returns TestLoginData.SSID.value
        }

        @Provides
        @Singleton
        fun provideWifiConfiguration(): WifiConfiguration = WifiConfiguration()

        @Provides
        @Singleton
        fun provideGSON() = Gson()

        @Provides
        @Singleton
        fun provideLibVLC(@ApplicationContext context: Context): LibVLC = LibVLC(context)

        @Provides
        @Singleton
        fun provideMediaPlayer(libVLC: LibVLC): MediaPlayer = MediaPlayer(libVLC)

        @Provides
        @Singleton
        fun provideVLCMediaPlayer(libVLC: LibVLC, mediaPlayer: MediaPlayer): VLCMediaPlayer =
            mockk(relaxed = true) {
                every { changeAspectRatio() } returns Unit
                every { createMediaPlayer(any(), any()) } returns Unit
                every { setSizeInMediaPlayer(any()) } returns Unit
                every { playMediaPlayer() } returns Unit
                every { stopMediaPlayer() } returns Unit
                every { pauseMediaPlayer() } returns Unit
                every { setProgressMediaPlayer(any()) } returns Unit
                every { isMediaPlayerPlaying() } returns false
                every { getTimeInMillisMediaPlayer() } returns 1000L andThenMany (listOf(
                    2000L,
                    3000L,
                    4000L,
                    5000L,
                    8000L,
                    10000L
                ))
            }

        @Provides
        @Singleton
        fun provideMobileDataStatus(connectivityManager: ConnectivityManager) =
            mockk<MobileDataStatus>(relaxed = true) {
                every { value } returns false
            }

    }
}
