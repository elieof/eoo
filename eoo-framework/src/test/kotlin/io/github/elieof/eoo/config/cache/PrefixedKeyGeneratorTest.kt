package io.github.elieof.eoo.config.cache

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import java.util.*

class PrefixedKeyGeneratorTest {

    @Test
    fun generatePrefixFromShortCommitId() {
        val gitProperties = Properties()
        gitProperties["commit.id.abbrev"] = "1234"
        val prefixedKeyGenerator = PrefixedKeyGenerator(GitProperties(gitProperties), null)
        assertThat(prefixedKeyGenerator.prefix).isEqualTo("1234")
    }

    @Test
    fun generatePrefixFromCommitId() {
        val gitProperties = Properties()
        gitProperties["commit.id"] = "1234567"
        val prefixedKeyGenerator = PrefixedKeyGenerator(GitProperties(gitProperties), null)
        assertThat(prefixedKeyGenerator.prefix).isEqualTo("1234567")
    }

    @Test
    fun generatePrefixFromBuildVersion() {
        val buildProperties = Properties()
        buildProperties["version"] = "1.0.0"
        val prefixedKeyGenerator = PrefixedKeyGenerator(null, BuildProperties(buildProperties))
        assertThat(prefixedKeyGenerator.prefix).isEqualTo("1.0.0")
    }

    @Test
    fun generatePrefixFromBuildTime() {
        val buildProperties = Properties()
        buildProperties["time"] = "1583955265"
        val prefixedKeyGenerator = PrefixedKeyGenerator(null, BuildProperties(buildProperties))
        assertThat(prefixedKeyGenerator.prefix).isEqualTo("1970-01-19T07:59:15.265Z")
    }

    @Test
    fun generatesRandomPrefix() {
        val prefixedKeyGenerator = PrefixedKeyGenerator(null, null)
        assertThat(prefixedKeyGenerator.prefix.length).isEqualTo(12)
    }

    @Test
    fun generatesRandomPrefixKey() {
        val gitProperties = Properties()
        gitProperties["commit.id"] = "1234567"
        val prefixedKeyGenerator = PrefixedKeyGenerator(GitProperties(gitProperties), null)
        val method = String::class.java.getMethod("equalsIgnoreCase", String::class.java)
        assertThat(prefixedKeyGenerator.generate("target", method, "test")).isEqualTo(
            PrefixedSimpleKey("1234567", "equalsIgnoreCase", listOf("test"))
        )
    }
}
