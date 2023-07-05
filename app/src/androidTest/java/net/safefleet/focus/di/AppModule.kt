@file:Suppress("DEPRECATION")

package net.safefleet.focus.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.lawmobile.data.dto.interceptors.RequestInterceptor
import com.lawmobile.data.extensions.addInterceptor
import com.lawmobile.data.extensions.installKotlinxSerializer
import com.lawmobile.database.Database
import com.lawmobile.domain.utils.PreferencesManager
import com.lawmobile.presentation.connectivity.MobileDataStatus
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.connectivity.WifiStatus
import com.lawmobile.presentation.extensions.dataStore
import com.lawmobile.presentation.utils.PreferencesManagerImpl
import com.safefleet.mobile.kotlin_commons.helpers.network_manager.ListenableNetworkManager
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import net.safefleet.focus.helpers.SimpleTestNetworkManager
import net.safefleet.focus.helpers.WifiHelperMock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    companion object {

        @Provides
        @Singleton
        fun providePreferencesDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = context.dataStore

        @Provides
        @Singleton
        fun providePreferencesManager(
            dataStore: DataStore<Preferences>
        ): PreferencesManager = PreferencesManagerImpl(dataStore)

        @Provides
        @Singleton
        fun provideRequestInterceptor(preferencesManager: PreferencesManager) =
            RequestInterceptor(preferencesManager)

        @Provides
        @Singleton
        fun provideHttpClient(interceptor: RequestInterceptor): HttpClient =
            HttpClient(Android) {
                installKotlinxSerializer()
            }.apply {
                addInterceptor(interceptor)
            }

        @Provides
        @Singleton
        fun provideSimpleNetworkManager(): ListenableNetworkManager = SimpleTestNetworkManager()

        @Provides
        @Singleton
        fun provideBackgroundDispatcher() = Dispatchers.IO

        @Provides
        fun provideJob(): Job = Job()

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
            mockk(relaxed = true) {
                every {
                    registerNetworkCallback(
                        any<NetworkRequest>(),
                        any<ConnectivityManager.NetworkCallback>()
                    )
                } just Runs
                every {
                    requestNetwork(
                        any<NetworkRequest>(),
                        any<ConnectivityManager.NetworkCallback>()
                    )
                } just Runs
                every { bindProcessToNetwork(any()) } returns true
            }

        @Provides
        @Singleton
        fun provideWifiHelper(
            wifiManager: WifiManager,
            connectivityManager: ConnectivityManager
        ): WifiHelper = WifiHelperMock()

        @Provides
        @Singleton
        fun provideWifiConfiguration(): WifiConfiguration = WifiConfiguration()

        @Provides
        @Singleton
        fun provideGSON() = Gson()

        @Provides
        @Singleton
        fun provideMobileDataStatus(connectivityManager: ConnectivityManager) =
            mockk<MobileDataStatus>(relaxed = true) {
                every { value } returns false
            }

        @Provides
        @Singleton
        fun provideWifiStatus(connectivityManager: ConnectivityManager) =
            WifiStatus(connectivityManager)
    }
}
