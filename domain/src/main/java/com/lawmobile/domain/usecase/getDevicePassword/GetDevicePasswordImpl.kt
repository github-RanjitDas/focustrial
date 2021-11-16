package com.lawmobile.domain.usecase.getDevicePassword

import com.lawmobile.domain.repository.user.UserRepository
import com.lawmobile.domain.validator.DevicePasswordValidator
import com.safefleet.mobile.kotlin_commons.helpers.Result

class GetDevicePasswordImpl(
    private val userRepository: UserRepository
) : GetDevicePassword {
    override suspend fun invoke(uuid: String): Result<String> {
        return try {
            when (val result = userRepository.getUserFromNetwork(uuid)) {
                is Result.Success -> {
                    val devicePassword = DevicePasswordValidator(result.data.devicePassword)
                    Result.Success(devicePassword)
                }
                is Result.Error -> result
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
