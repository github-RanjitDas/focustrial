package com.lawmobile.presentation.ui.bodyWornDiagnosis

import com.lawmobile.domain.usecase.bodyWornDiagnosis.BodyWornDiagnosisUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class BodyWornDiagnosisViewModelTest {

    private val useCaseDiagnosis: BodyWornDiagnosisUseCase = mockk()
    private val viewModel by lazy {
        BodyWornDiagnosisViewModel(useCaseDiagnosis)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testIsDiagnosisResponseTrue() = runBlockingTest {
        coEvery { useCaseDiagnosis.isDiagnosisSuccess() } returns Result.Success(true)
        viewModel.getDiagnosis()
        val response = viewModel.diagnosisCameraLiveData.value
        Assert.assertEquals(response, Result.Success(true))
        coVerify { useCaseDiagnosis.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisResponseFalse() = runBlockingTest {
        coEvery { useCaseDiagnosis.isDiagnosisSuccess() } returns Result.Success(false)
        viewModel.getDiagnosis()
        val response = viewModel.diagnosisCameraLiveData.value
        Assert.assertEquals(response, Result.Success(false))
        coVerify { useCaseDiagnosis.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisError() = runBlockingTest {
        coEvery { useCaseDiagnosis.isDiagnosisSuccess() } returns Result.Error(Exception(""))
        viewModel.getDiagnosis()
        val response = viewModel.diagnosisCameraLiveData.value
        Assert.assertTrue(response is Result.Error)
        coVerify { useCaseDiagnosis.isDiagnosisSuccess() }
    }
}
