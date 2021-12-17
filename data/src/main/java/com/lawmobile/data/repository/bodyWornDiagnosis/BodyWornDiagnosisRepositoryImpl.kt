package com.lawmobile.data.repository.bodyWornDiagnosis

import com.lawmobile.data.datasource.remote.bodyWornDiagnosis.BodyWornDiagnosisDataSource
import com.lawmobile.domain.repository.bodyWornDiagnosis.BodyWornDiagnosisRepository

class BodyWornDiagnosisRepositoryImpl(
    private val bodyWornDiagnosisDataSource: BodyWornDiagnosisDataSource
) : BodyWornDiagnosisRepository {
    override suspend fun isDiagnosisSuccess() = bodyWornDiagnosisDataSource.isDiagnosisSuccess()
}
