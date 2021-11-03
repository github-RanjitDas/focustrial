package com.safefleet.lawmobile.di.login

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.lawmobile.presentation.utils.AuthStateManagerFactory
import com.lawmobile.presentation.utils.AuthStateManagerFactoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService

@InstallIn(SingletonComponent::class)
@Module
class LoginModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("main", Context.MODE_PRIVATE)

    @Provides
    fun provideAuthorizationService(@ApplicationContext context: Context) =
        AuthorizationService(context)

    @Provides
    fun provideAuthStateManagerFactory(
        sharedPreferences: SharedPreferences,
        dataStore: DataStore<Preferences>,
        authorizationService: AuthorizationService
    ): AuthStateManagerFactory =
        AuthStateManagerFactoryImpl(sharedPreferences, dataStore, authorizationService)
}
