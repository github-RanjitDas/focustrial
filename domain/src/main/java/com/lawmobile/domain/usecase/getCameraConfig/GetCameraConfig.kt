package com.lawmobile.domain.usecase.getCameraConfig

import com.lawmobile.domain.entities.Config
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface GetCameraConfig {
    suspend operator fun invoke(): Result<Config>
}
