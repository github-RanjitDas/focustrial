package com.safefleet.lawmobile.di.authorization

import com.lawmobile.data.datasource.remote.authorization.AuthorizationRemoteDataSource
import com.lawmobile.data.datasource.remote.authorization.AuthorizationRemoteDataSourceImpl
import com.lawmobile.data.dto.api.authorization.AuthorizationApi
import com.lawmobile.data.dto.api.authorization.AuthorizationApiImpl
import com.lawmobile.data.repository.authorization.AuthorizationRepositoryImpl
import com.lawmobile.domain.repository.authorization.AuthorizationRepository
import com.lawmobile.domain.usecase.authorization.AuthorizationUseCase
import com.lawmobile.domain.usecase.authorization.AuthorizationUseCaseImpl
import com.safefleet.lawmobile.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
object AuthorizationEndpointsModule {

    @Provides
    fun provideAuthorizationEndpointsApi(
        @Named("fakeHttpClient") httpClient: HttpClient
    ): AuthorizationApi = AuthorizationApiImpl(BuildConfig.DISCOVERY_USER_URL, httpClient)

    @Provides
    fun provideAuthorizationEndpointsRemoteDataSource(
        authorizationApi: AuthorizationApi
    ): AuthorizationRemoteDataSource =
        AuthorizationRemoteDataSourceImpl(authorizationApi)

    @Provides
    fun provideAuthorizationEndpointsRepository(
        authorizationRemoteDataSource: AuthorizationRemoteDataSource
    ): AuthorizationRepository =
        AuthorizationRepositoryImpl(authorizationRemoteDataSource)

    @Provides
    fun provideAuthorizationEndpointsUseCase(
        authorizationRepository: AuthorizationRepository
    ): AuthorizationUseCase = AuthorizationUseCaseImpl(authorizationRepository)
}
