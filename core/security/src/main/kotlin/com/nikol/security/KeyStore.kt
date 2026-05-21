package com.nikol.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeyStore {
    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_TYPE).apply {
        load(null)
    }

    val keyGenParameterSpec = KeyGenParameterSpec
        .Builder(
            KEY_ALIES,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        .setBlockModes(BLOCK_MODE)
        .setEncryptionPaddings(ENCRYPTION_PADDING)
        .setKeySize(256)
        .build()

    init {
        if (!keyStore.containsAlias(KEY_ALIES)) {
            generateKey()
        }
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val iv = cipher.iv

        val encryptedBytes = cipher.doFinal(data.toByteArray())
        val combined = ByteArray(iv.size + encryptedBytes.size)

        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String): String {
        val combined = Base64.decode(encryptedData, Base64.DEFAULT)

        val iv = ByteArray(IV_SIZE)
        System.arraycopy(combined, 0, iv, 0, iv.size)

        val encryptedBytes = ByteArray(combined.size - iv.size)
        System.arraycopy(combined, iv.size, encryptedBytes, 0, encryptedBytes.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

        return String(cipher.doFinal(encryptedBytes))
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            ALGORITHM,
            ANDROID_KEY_STORE_TYPE
        )

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getKey(): SecretKey {
        val entry = keyStore.getEntry(KEY_ALIES, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    private companion object Companion {
        private const val ANDROID_KEY_STORE_TYPE = "AndroidKeyStore"
        private const val KEY_ALIES = "default_key"
        private const val IV_SIZE = 12

        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$ENCRYPTION_PADDING"
    }
}