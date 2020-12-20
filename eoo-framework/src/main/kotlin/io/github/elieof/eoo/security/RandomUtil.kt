package io.github.elieof.eoo.security

import org.apache.commons.lang.RandomStringUtils
import java.security.SecureRandom


/**
 * Utility class for generating random Strings.
 */
object RandomUtil {
    private const val DEF_COUNT = 20
    private const val CAPACITY = 64
    private val SECURE_RANDOM: SecureRandom = SecureRandom()

    init {
        SECURE_RANDOM.nextBytes(ByteArray(CAPACITY))
    }


    /**
     *
     * generateRandomAlphanumericString.
     *
     * @return a [java.lang.String] object.
     */
    private fun generateRandomAlphanumericString(): String {
        return RandomStringUtils.random(DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM)
    }

    /**
     * Generate a password.
     *
     * @return the generated password.
     */
    @JvmStatic
    fun generatePassword(): String {
        return generateRandomAlphanumericString()
    }

    /**
     * Generate an activation key.
     *
     * @return the generated activation key.
     */
    @JvmStatic
    fun generateActivationKey(): String {
        return generateRandomAlphanumericString()
    }

    /**
     * Generate a reset key.
     *
     * @return the generated reset key.
     */
    @JvmStatic
    fun generateResetKey(): String {
        return generateRandomAlphanumericString()
    }

}
