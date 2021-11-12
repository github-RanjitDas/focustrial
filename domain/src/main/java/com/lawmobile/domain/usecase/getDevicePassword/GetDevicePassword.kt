package com.lawmobile.domain.usecase.getDevicePassword

import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface GetDevicePassword : BaseUseCase {
    suspend operator fun invoke(uuid: String): Result<String>
}
