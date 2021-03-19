package com.lawmobile.data.repository.bodyWornDiagnosis

import com.lawmobile.data.datasource.remote.bodyWornDiagnosis.BodyWornDiagnosisDataSource
import com.lawmobile.domain.repository.bodyWornDiagnosis.BodyWornDiagnosisRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornDiagnosisRepositoryImpl(private val bodyWornDiagnosisDataSource: BodyWornDiagnosisDataSource) :
    BodyWornDiagnosisRepository {
    override suspend fun isDiagnosisSuccess(): Result<Boolean> {
        return bodyWornDiagnosisDataSource.isDiagnosisSuccess()
    }
}
