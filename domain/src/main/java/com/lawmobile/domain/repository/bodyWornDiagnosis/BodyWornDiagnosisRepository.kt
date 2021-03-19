package com.lawmobile.domain.repository.bodyWornDiagnosis

import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface BodyWornDiagnosisRepository : BaseRepository {
    suspend fun isDiagnosisSuccess(): Result<Boolean>
}
