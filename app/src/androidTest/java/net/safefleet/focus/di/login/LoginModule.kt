package net.safefleet.focus.di.login

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.SharedPreferences
import com.lawmobile.domain.repository.authorization.AuthorizationRepository
import com.lawmobile.domain.repository.user.UserRepository
import com.lawmobile.domain.usecase.LoginUseCases
import com.lawmobile.domain.usecase.getAuthorizationEndpoints.GetAuthorizationEndpoints
import com.lawmobile.domain.usecase.getAuthorizationEndpoints.GetAuthorizationEndpointsImpl
import com.lawmobile.domain.usecase.getDevicePassword.GetDevicePassword
import com.lawmobile.domain.usecase.getDevicePassword.GetDevicePasswordImpl
import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCamera
import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCameraImpl
import com.lawmobile.presentation.authentication.AuthStateManagerFactory
import com.lawmobile.presentation.authentication.AuthStateManagerFactoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mockk.every
import io.mockk.mockk
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
    fun provideGetAuthorizationEndpoints(
        authorizationRepository: AuthorizationRepository
    ): GetAuthorizationEndpoints = GetAuthorizationEndpointsImpl(authorizationRepository)

    @Provides
    fun provideGetDevicePassword(
        userRepository: UserRepository
    ): GetDevicePassword = GetDevicePasswordImpl(userRepository)

    @Provides
    fun provideGetUserFromCamera(
        userRepository: UserRepository
    ): GetUserFromCamera = GetUserFromCameraImpl(userRepository)

    @Provides
    fun provideLoginUseCases(
        getAuthorizationEndpoints: GetAuthorizationEndpoints,
        getDevicePassword: GetDevicePassword,
        getUserFromCamera: GetUserFromCamera
    ) = LoginUseCases(getAuthorizationEndpoints, getDevicePassword, getUserFromCamera)

    @Provides
    fun provideAuthStateManagerFactory(
        sharedPreferences: SharedPreferences,
        authorizationService: AuthorizationService
    ): AuthStateManagerFactory =
        AuthStateManagerFactoryImpl(sharedPreferences, authorizationService)

    @Provides
    fun provideBluetoothAdapter(): BluetoothAdapter = mockk {
        every { isEnabled } returns true
    }
}
