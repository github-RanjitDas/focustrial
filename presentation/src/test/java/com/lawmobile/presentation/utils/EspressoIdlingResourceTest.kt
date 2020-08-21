package com.lawmobile.presentation.utils

import io.mockk.clearAllMocks
import io.mockk.mockkObject
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EspressoIdlingResourceTest {

    @BeforeEach
    fun setup() {
        mockkObject(EspressoIdlingResource)
    }

    @AfterEach
    fun clear() {
        clearAllMocks()
    }

    @Test
    fun increment() {
        EspressoIdlingResource.increment()
        Assert.assertFalse(EspressoIdlingResource.countingIdlingResource.isIdleNow)
    }

    @Test
    fun decrementNotIdle() {
        if (EspressoIdlingResource.countingIdlingResource.isIdleNow) {
            EspressoIdlingResource.increment()
        }
        EspressoIdlingResource.decrement()
        Assert.assertTrue(EspressoIdlingResource.countingIdlingResource.isIdleNow)
    }

    @Test
    fun decrementIsIdle() {
        while (!EspressoIdlingResource.countingIdlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        Assert.assertTrue(EspressoIdlingResource.countingIdlingResource.isIdleNow)
    }
}