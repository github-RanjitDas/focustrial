package com.lawmobile.data.datasource.remote.bodyWornDiagnosis

import com.safefleet.mobile.kotlin_commons.helpers.Result

interface BodyWornDiagnosisDataSource {
    suspend fun isDiagnosisSuccess(): Result<Boolean>
}
