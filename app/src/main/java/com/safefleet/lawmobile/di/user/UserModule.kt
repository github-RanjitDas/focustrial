package com.safefleet.lawmobile.di.user

import com.lawmobile.data.datasource.remote.user.UserRemoteDataSource
import com.lawmobile.data.datasource.remote.user.UserRemoteDataSourceImpl
import com.lawmobile.data.dto.api.user.UserApi
import com.lawmobile.data.dto.api.user.UserApiImpl
import com.lawmobile.data.repository.user.UserRepositoryImpl
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.repository.user.UserRepository
import com.lawmobile.domain.utils.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient

@InstallIn(SingletonComponent::class)
@Module
class UserModule {

    @Provides
    fun provideUserApi(httpClient: HttpClient, preferencesManager: PreferencesManager): UserApi =
        UserApiImpl(httpClient, preferencesManager)

    @Provides
    fun provideUserRemoteDataSource(
        cameraServiceFactory: CameraServiceFactory,
        userApi: UserApi
    ): UserRemoteDataSource = UserRemoteDataSourceImpl(cameraServiceFactory, userApi)

    @Provides
    fun provideUserRepository(userRemoteDataSource: UserRemoteDataSource): UserRepository =
        UserRepositoryImpl(userRemoteDataSource)
}
