package net.safefleet.focus.di.authorization

import com.lawmobile.data.datasource.local.authorization.AuthorizationLocalDataSource
import com.lawmobile.data.datasource.local.authorization.AuthorizationLocalDataSourceImpl
import com.lawmobile.data.datasource.remote.authorization.AuthorizationRemoteDataSource
import com.lawmobile.data.datasource.remote.authorization.AuthorizationRemoteDataSourceImpl
import com.lawmobile.data.dto.api.authorization.AuthorizationApi
import com.lawmobile.data.dto.api.authorization.AuthorizationApiImpl
import com.lawmobile.data.repository.authorization.AuthorizationRepositoryImpl
import com.lawmobile.domain.repository.authorization.AuthorizationRepository
import com.lawmobile.domain.utils.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient

@InstallIn(SingletonComponent::class)
@Module
object AuthorizationModule {

    @Provides
    fun provideAuthorizationApi(
        httpClient: HttpClient
    ): AuthorizationApi = AuthorizationApiImpl(httpClient)

    @Provides
    fun provideAuthorizationRemoteDataSource(
        authorizationApi: AuthorizationApi
    ): AuthorizationRemoteDataSource =
        AuthorizationRemoteDataSourceImpl(authorizationApi)

    @Provides
    fun provideAuthorizationLocalDataSource(
        preferencesManager: PreferencesManager
    ): AuthorizationLocalDataSource =
        AuthorizationLocalDataSourceImpl(preferencesManager)

    @Provides
    fun provideAuthorizationRepository(
        authorizationRemoteDataSource: AuthorizationRemoteDataSource,
        authorizationLocalDataSource: AuthorizationLocalDataSource
    ): AuthorizationRepository =
        AuthorizationRepositoryImpl(authorizationRemoteDataSource, authorizationLocalDataSource)
}
