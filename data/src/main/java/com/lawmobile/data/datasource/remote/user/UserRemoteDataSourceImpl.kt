package com.lawmobile.data.datasource.remote.user

import com.lawmobile.data.dto.api.user.UserApi
import com.lawmobile.data.dto.entities.UserDto
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result

class UserRemoteDataSourceImpl(
    cameraServiceFactory: CameraServiceFactory,
    private val userApi: UserApi
) : UserRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

    override suspend fun getUserFromNetwork(uuid: String): UserDto = userApi.getUser(uuid)

    override suspend fun getUserFromCamera(): CameraUser {
        return when (val user = cameraService.getUserResponse()) {
            is Result.Success -> user.data
            is Result.Error -> throw user.exception
        }
    }
}
