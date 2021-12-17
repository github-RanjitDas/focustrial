package com.lawmobile.domain.validator

import org.junit.Assert
import org.junit.jupiter.api.Test

internal class DevicePasswordValidatorTest {

    @Test
    fun invokeSuccess() {
        val devicePassword = "123"
        val result = DevicePasswordValidator(devicePassword)
        Assert.assertEquals(devicePassword, result)
    }

    @Test
    fun invokeEmptyError() {
        val devicePassword = ""
        val result = try {
            DevicePasswordValidator(devicePassword)
        } catch (e: Exception) {
            e
        }
        Assert.assertNotEquals(devicePassword, result)
    }

    @Test
    fun invokeNullError() {
        val devicePassword = null
        val result = try {
            DevicePasswordValidator(devicePassword)
        } catch (e: Exception) {
            e
        }
        Assert.assertNotEquals(devicePassword, result)
    }
}
