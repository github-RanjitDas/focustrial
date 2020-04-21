package com.lawmobile.presentation.utils

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EncodePasswordTest {

    @Test
    fun encodePasswordOfficer() {
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), Base64.DEFAULT) } returns "9508"
        Assert.assertEquals(
            PASSWORD_DECRYPTED,
            EncodePassword.encodePasswordOfficer(PASSWORD_ENCRYPTED)
        )
    }

    @Test
    fun encodePasswordOfficerFailed() {
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), Base64.DEFAULT) } throws Exception()
        Assert.assertEquals(
            "",
            EncodePassword.encodePasswordOfficer("")
        )
    }

    companion object {
        const val PASSWORD_ENCRYPTED = "hgElMu+nDSlTmaGC9F9QUbf/HY4WjnQ4YKHHiO1BCiU="
        const val PASSWORD_DECRYPTED = "9508"
    }
}