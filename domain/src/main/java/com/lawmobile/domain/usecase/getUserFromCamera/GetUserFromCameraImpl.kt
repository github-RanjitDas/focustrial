package com.lawmobile.domain.usecase.getUserFromCamera

import com.lawmobile.domain.entities.User
import com.lawmobile.domain.repository.user.UserRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class GetUserFromCameraImpl(
    private val userRepository: UserRepository
) : GetUserFromCamera {
    override suspend fun invoke(): Result<User> = userRepository.getUserFromCamera()
}
