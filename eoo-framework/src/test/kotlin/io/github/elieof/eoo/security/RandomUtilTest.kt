package io.github.elieof.eoo.security

import org.apache.commons.lang.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RandomUtilTest {

    @Test
    fun generateString() {
        assertStringIsAlphaNumeric(RandomUtil.generatePassword())
    }

    @Test
    fun generateActivationKey() {
        assertStringIsAlphaNumeric(RandomUtil.generateActivationKey())
    }

    @Test
    fun generateResetKey() {
        assertStringIsAlphaNumeric(RandomUtil.generateResetKey())
    }

    private fun assertStringIsAlphaNumeric(generateString: String) {
        assertThat(generateString).isNotEmpty.hasSize(20).doesNotContainAnyWhitespaces()
        assertTrue { StringUtils.isAlphanumeric(generateString) }
    }
}
