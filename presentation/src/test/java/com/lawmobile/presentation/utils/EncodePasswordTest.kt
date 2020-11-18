package com.lawmobile.presentation.utils

import android.util.Base64
import io.mockk.*
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.security.MessageDigest


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EncodePasswordTest {

    @Test
    fun encodePasswordOfficer() {
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), Base64.DEFAULT) } returns "9508"

        mockkStatic(MessageDigest::class)
        val mdDigest: MessageDigest = mockk()
        every { MessageDigest.getInstance("SHA-256") } returns mdDigest
        every { mdDigest.update(PASSWORD_ENCRYPTED.toByteArray()) } just Runs
        every { mdDigest.digest() } returns PASSWORD_ENCRYPTED.toByteArray()

        val encodePassword = EncodePassword.encodePasswordOfficer(PASSWORD_ENCRYPTED)
        verify { mdDigest.update(PASSWORD_ENCRYPTED.toByteArray()) }
        Assert.assertEquals(PASSWORD_DECRYPTED, encodePassword)

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