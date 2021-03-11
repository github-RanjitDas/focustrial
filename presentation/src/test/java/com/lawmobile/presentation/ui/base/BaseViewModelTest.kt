package com.lawmobile.presentation.ui.base

import com.lawmobile.presentation.InstantExecutorExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class BaseViewModelTest {

    private val baseViewModel: BaseViewModel by lazy {
        BaseViewModel()
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun waitToFinish() {
        baseViewModel.waitToFinish(100)
        runBlocking {
            delay(150)
            Assert.assertTrue(baseViewModel.isWaitFinishedLiveData.value!!.getContent())
        }
    }
}
