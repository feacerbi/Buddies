package com.buddies.security.encryption

import com.buddies.security.usecase.SecurityUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

class Encrypter(
    private val securityUseCases: SecurityUseCases
) {

    private val cipher = Cipher.getInstance(ALGORITHM_WITH_PADDING)

    suspend fun encrypt(
        plaintext: String?
    ): String {
        val specs = getKeySpecs()

        cipher.init(Cipher.ENCRYPT_MODE, specs.first, specs.second)
        val encryptedText: ByteArray? = cipher.doFinal(plaintext?.toByteArray())

        return encryptedText.toEncryptedString()
    }

    suspend fun decrypt(
        encryptedText: String?
    ): String {
        val specs = getKeySpecs()

        cipher.init(Cipher.DECRYPT_MODE, specs.first, specs.second)
        val decryptedText = cipher.doFinal(encryptedText.toEncryptedByteArray())

        return decryptedText.toString(UTF_8)
    }

    fun generateNewKey() =
        KeyGenerator.getInstance(ALGORITHM).apply {
            init(GENERATOR_KEY_SIZE)
        }.generateKey()

    fun generateNewIv() = ByteArray(IV_SIZE).apply {
        SecureRandom().nextBytes(this)
    }

    private suspend fun getKeySpecs(): Pair<SecretKeySpec, IvParameterSpec> {
        val secretKey = withContext(Dispatchers.IO) { securityUseCases.fetchEncryptionKey() }

        val key = secretKey?.info?.key.toEncryptedByteArray()
        val iv = secretKey?.info?.iv.toEncryptedByteArray()

        val keySpec = SecretKeySpec(key, ALGORITHM_WITH_PADDING)
        val ivSpec = IvParameterSpec(iv)

        return Pair(keySpec, ivSpec)
    }

    private fun String?.toEncryptedByteArray() = this
        ?.split(ENCRYPTED_SEPARATOR)
        ?.map { it.toByte() }
        ?.toByteArray()
        ?: byteArrayOf()

    private fun ByteArray?.toEncryptedString() = this
        ?.toList()
        ?.joinToString(separator = ENCRYPTED_SEPARATOR)
        ?: ""

    companion object {
        private const val GENERATOR_KEY_SIZE = 256
        private const val IV_SIZE = 16
        private const val ALGORITHM = "AES"
        private const val ALGORITHM_WITH_PADDING = "AES/CBC/PKCS5Padding"
        private const val ENCRYPTED_SEPARATOR = "|"
    }
}