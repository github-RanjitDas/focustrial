package com.lawmobile.domain.usecase.getUserFromCamera

import com.lawmobile.domain.entities.User
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface GetUserFromCamera : BaseUseCase {
    suspend operator fun invoke(): Result<User>
}
