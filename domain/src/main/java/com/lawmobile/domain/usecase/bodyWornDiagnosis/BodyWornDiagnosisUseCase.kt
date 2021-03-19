package com.lawmobile.domain.usecase.bodyWornDiagnosis

import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface BodyWornDiagnosisUseCase : BaseUseCase {
    suspend fun isDiagnosisSuccess(): Result<Boolean>
}
