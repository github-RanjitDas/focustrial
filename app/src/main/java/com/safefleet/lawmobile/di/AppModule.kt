@file:Suppress("DEPRECATION")

package com.safefleet.lawmobile.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import com.google.gson.Gson
import com.lawmobile.database.Database
import com.lawmobile.presentation.utils.MobileDataStatus
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.lawmobile.presentation.utils.WifiHelper
import com.lawmobile.presentation.utils.WifiStatus
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    companion object {

        @Provides
        @Singleton
        fun provideBackgroundDispatcher() = Dispatchers.IO

        @Provides
        @Singleton
        fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver =
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = context,
                name = "Database.db"
            )

        @Provides
        @Singleton
        fun provideDatabase(driver: SqlDriver): Database = Database(driver)

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
        @Singleton
        fun provideWifiHelper(wifiManager: WifiManager): WifiHelper =
            WifiHelper(wifiManager)

        @Provides
        @Singleton
        fun provideWifiConfiguration(): WifiConfiguration = WifiConfiguration()

        @Provides
        @Singleton
        fun provideGSON() = Gson()

        @Provides
        fun provideLibVLC(@ApplicationContext context: Context): LibVLC = LibVLC(context)

        @Provides
        fun provideMediaPlayer(libVLC: LibVLC): MediaPlayer = MediaPlayer(libVLC)

        @Provides
        fun provideVLCMediaPlayer(libVLC: LibVLC, mediaPlayer: MediaPlayer): VLCMediaPlayer =
            VLCMediaPlayer(libVLC, mediaPlayer)

        @Provides
        @Singleton
        fun provideMobileDataStatus(connectivityManager: ConnectivityManager) =
            MobileDataStatus(connectivityManager)

        @Provides
        @Singleton
        fun provideWifiStatus(connectivityManager: ConnectivityManager) =
            WifiStatus(connectivityManager)
    }
}
