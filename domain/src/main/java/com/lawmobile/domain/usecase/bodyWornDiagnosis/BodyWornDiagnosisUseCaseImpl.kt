package com.lawmobile.domain.usecase.bodyWornDiagnosis

import com.lawmobile.domain.repository.bodyWornDiagnosis.BodyWornDiagnosisRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class BodyWornDiagnosisUseCaseImpl(private val bodyWornDiagnosisRepository: BodyWornDiagnosisRepository) :
    BodyWornDiagnosisUseCase {
    override suspend fun isDiagnosisSuccess(): Result<Boolean> {
        return bodyWornDiagnosisRepository.isDiagnosisSuccess()
    }
}
