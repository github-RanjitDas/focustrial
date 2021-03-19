package com.lawmobile.data.datasource.remote.bodyWornDiagnosis

import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BodyWornDiagnosisDataSourceImplTest {

    private val bodyWornDiagnosisDataSourceImpl = BodyWornDiagnosisDataSourceImpl()

    @Test
    fun testIsDiagnosisSuccess() {
        runBlocking {
            val firstAttempt = bodyWornDiagnosisDataSourceImpl.isDiagnosisSuccess()
            Assert.assertEquals(firstAttempt, Result.Success(true))

            val secondAttempt = bodyWornDiagnosisDataSourceImpl.isDiagnosisSuccess()
            Assert.assertEquals(secondAttempt, Result.Success(false))
        }
    }
}
