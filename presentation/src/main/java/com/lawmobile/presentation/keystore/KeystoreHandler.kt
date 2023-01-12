package com.lawmobile.presentation.keystore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.UnsupportedOperationException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.security.auth.x500.X500Principal

object KeystoreHandler {
    private const val TAG = "KeystoreHandler"
    private const val alias = "Sfkeys1"
    private const val keyStoreType = "AndroidKeyStore"

    fun storeConfigInKeystore(context: Context, plainText: String) {
        try {
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null)
            // Create the keys if necessary
            if (!keyStore.containsAlias(alias)) {
                val notBefore = Calendar.getInstance()
                val notAfter = Calendar.getInstance()
                notAfter.add(Calendar.YEAR, 1)
                val spec = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, keyStoreType
                )
                spec.initialize(
                    KeyGenParameterSpec.Builder(
                        alias,
                        KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT
                    )
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1) //  RSA/ECB/PKCS1Padding
                        .setKeySize(2048) // *** Replaced: setStartDate
                        .setKeyValidityStart(notBefore.time) // *** Replaced: setEndDate
                        .setKeyValidityEnd(notAfter.time) // *** Replaced: setSubject
                        .setCertificateSubject(X500Principal("CN=test")) // *** Replaced: setSerialNumber
                        .setCertificateSerialNumber(BigInteger.ONE).build()
                )
                val keyPair = spec.generateKeyPair()
                Log.i(TAG, keyPair.toString())
            }

            val privateKey = keyStore.getKey(alias, null) as PrivateKey
            val publicKey = keyStore.getCertificate(alias).publicKey
            val encryptedDataFilePath = getEncryptedFilePath(context)
            Log.v(TAG, "encryptedDataFilePath = $encryptedDataFilePath")

            // *** Changed the padding type here and changed to AndroidKeyStoreBCWorkaround
            val inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround")
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey)

            // *** Changed the padding type here and changed to AndroidKeyStoreBCWorkaround
            val outCipher =
                Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround")
            outCipher.init(Cipher.DECRYPT_MODE, privateKey)
            val cipherOutputStream =
                CipherOutputStream(FileOutputStream(encryptedDataFilePath), inCipher)
            // *** Replaced string literal with StandardCharsets.UTF_8
            cipherOutputStream.write(plainText.toByteArray(StandardCharsets.UTF_8))
            cipherOutputStream.close()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: UnrecoverableEntryException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
    }

    fun getConfigFromKeystore(context: Context): String? {
        try {
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null)
            val encryptedDataFilePath = getEncryptedFilePath(context)
            val privateKey = keyStore.getKey(alias, null) as PrivateKey
            val outCipher =
                Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround")
            outCipher.init(Cipher.DECRYPT_MODE, privateKey)
            val cipherInputStream =
                CipherInputStream(FileInputStream(encryptedDataFilePath), outCipher)
            val roundTrippedBytes = ByteArray(1000)
            var index = 0
            var nextByte: Int
            while (cipherInputStream.read().also { nextByte = it } != -1) {
                roundTrippedBytes[index] = nextByte.toByte()
                index++
            }
            return String(roundTrippedBytes, 0, index, StandardCharsets.UTF_8)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: java.lang.RuntimeException) {
            e.printStackTrace()
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: UnrecoverableEntryException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getEncryptedFilePath(context: Context): String {
        val filesDirectory = context.filesDir.absolutePath
        return filesDirectory + File.separator + "secrets"
    }
}
