package com.lawmobile.presentation.ui.bodyWornDiagnosis

import com.lawmobile.domain.usecase.bodyWornDiagnosis.BodyWornDiagnosisUseCase
import com.lawmobile.presentation.ui.bodyWornDiagnosis.state.DiagnosisState
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class BodyWornDiagnosisViewModelTest {

    private val useCaseDiagnosis: BodyWornDiagnosisUseCase = mockk()
    private val viewModel by lazy {
        BodyWornDiagnosisViewModel(useCaseDiagnosis)
    }

    private val dispatcher = TestCoroutineDispatcher()
    private val job by lazy {
        Job()
    }
    private val testScope by lazy {
        TestCoroutineScope(job + dispatcher)
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun clean() {
        job.cancel()
    }

    @Test
    fun testIsDiagnosisResponseTrue() {
        coEvery { useCaseDiagnosis.isDiagnosisSuccess() } returns Result.Success(true)

        testScope.launch {
            viewModel.getDiagnosis()
            val response = viewModel.diagnosisResult.first()
            Assert.assertEquals(response, Result.Success(true))
        }

        coVerify { useCaseDiagnosis.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisResponseFalse() {
        coEvery { useCaseDiagnosis.isDiagnosisSuccess() } returns Result.Success(false)

        testScope.launch {
            viewModel.getDiagnosis()
            val response = viewModel.diagnosisResult.first()
            Assert.assertEquals(response, Result.Success(false))
        }

        coVerify { useCaseDiagnosis.isDiagnosisSuccess() }
    }

    @Test
    fun testIsDiagnosisError() {
        coEvery { useCaseDiagnosis.isDiagnosisSuccess() } returns Result.Error(Exception(""))

        testScope.launch {
            viewModel.getDiagnosis()
            val response = viewModel.diagnosisResult.first()
            Assert.assertTrue(response is Result.Error)
        }

        coVerify { useCaseDiagnosis.isDiagnosisSuccess() }
    }

    @Test
    fun setDiagnosisState() {
        val state = DiagnosisState.Progress
        viewModel.setDiagnosisState(state)
        Assert.assertEquals(state, viewModel.diagnosisState.value)
    }

    @Test
    fun getDiagnosisState() {
        Assert.assertEquals(DiagnosisState.Start, viewModel.diagnosisState.value)
    }
}
