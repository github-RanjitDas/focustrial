package com.lawmobile.data.datasource.remote.bodyWornDiagnosis

import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay

class BodyWornDiagnosisDataSourceImpl : BodyWornDiagnosisDataSource {

    override suspend fun isDiagnosisSuccess(): Result<Boolean> {
        return returnDiagnosisMockWhileX2Supported()
    }

    private suspend fun returnDiagnosisMockWhileX2Supported(): Result<Boolean> {
        delay(3000)
        numberOfCallDiagnosis += 1
        return if (numberOfCallDiagnosis % 2 == 1) {
            Result.Success(true)
        } else {
            Result.Success(false)
        }
    }

    companion object {
        // Variable only for test, to can return true in mod 2 is 1 and false in other case
        private var numberOfCallDiagnosis = 0
    }
}
