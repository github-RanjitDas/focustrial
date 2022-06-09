package com.lawmobile.domain.usecase.saveFileBytes

import com.safefleet.mobile.kotlin_commons.helpers.Result

interface SaveFailSafeVideo {
    suspend operator fun invoke(fileBytes: ByteArray): Result<Unit>
}
