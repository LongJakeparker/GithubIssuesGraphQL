package com.example.githubissuesgraphql.util

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @author longtran
 * @since 09/06/2021
 */
object AESCrypt {
    private val ALGORITHM = "AES"

    @Throws(Exception::class)
    fun encrypt(value: String, key: String): String {
        val generatedKey = generateKey(key)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, generatedKey)
        val encryptedByteValue = cipher.doFinal(value.toByteArray(charset("utf-8")))
        return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT)

    }

    @Throws(Exception::class)
    fun decrypt(value: String, key: String): String {
        if (value.isEmpty())
            return ""

        val generatedKey = generateKey(key)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, generatedKey)
        val decryptedValue64 = Base64.decode(value, Base64.DEFAULT)
        val decryptedByteValue = cipher.doFinal(decryptedValue64)
        return String(decryptedByteValue, charset("utf-8"))
    }

    @Throws(Exception::class)
    private fun generateKey(key: String): Key {
        return SecretKeySpec(key.toByteArray(), ALGORITHM)
    }
}