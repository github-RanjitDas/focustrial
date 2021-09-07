package com.safefleet.lawmobile.di.validateOfficerId

import com.lawmobile.data.datasource.remote.validateOfficerId.ValidateOfficerIdRemoteDataSource
import com.lawmobile.data.datasource.remote.validateOfficerId.ValidateOfficerIdRemoteDataSourceImpl
import com.lawmobile.data.dto.api.ValidateOfficerIdApi
import com.lawmobile.data.dto.api.ValidateOfficerIdApiImpl
import com.lawmobile.data.repository.validateOfficerId.ValidateOfficerIdRepositoryImpl
import com.lawmobile.domain.repository.validateOfficerId.ValidateOfficerIdRepository
import com.lawmobile.domain.usecase.validateOfficerId.ValidateOfficerIdUseCase
import com.lawmobile.domain.usecase.validateOfficerId.ValidateOfficerIdUseCaseImpl
import com.safefleet.lawmobile.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
object ValidateOfficerIdModule {

    @Provides
    fun provideValidateOfficerIdApi(
        @Named("fakeHttpClient") httpClient: HttpClient
    ): ValidateOfficerIdApi = ValidateOfficerIdApiImpl(BuildConfig.VALIDATE_USER_URL, httpClient)

    @Provides
    fun provideValidateOfficerIdRemoteDataSource(
        validateOfficerIdApi: ValidateOfficerIdApi
    ): ValidateOfficerIdRemoteDataSource =
        ValidateOfficerIdRemoteDataSourceImpl(validateOfficerIdApi)

    @Provides
    fun provideValidateOfficerIdRepository(
        validateOfficerIdRemoteDataSource: ValidateOfficerIdRemoteDataSource
    ): ValidateOfficerIdRepository =
        ValidateOfficerIdRepositoryImpl(validateOfficerIdRemoteDataSource)

    @Provides
    fun provideValidateOfficerIdUseCase(
        validateOfficerIdRepository: ValidateOfficerIdRepository
    ): ValidateOfficerIdUseCase = ValidateOfficerIdUseCaseImpl(validateOfficerIdRepository)
}
