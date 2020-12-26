package io.github.elieof.eoo.config.cache

import org.apache.commons.lang.RandomStringUtils
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.cache.interceptor.KeyGenerator
import java.lang.reflect.Method
import java.time.format.DateTimeFormatter

/**
 * This class is responsible for generating cache keys that are specific to a version of the application
 * by prefixing them with git commit hash.
 *
 * This allows multiple versions of an application to "share" the same distributed cache even when the structure
 * of the values has changed between those versions of the software.
 *
 * This case typically occurs in production to ensure zero-downtime updates across a cluster
 * requiring that two different versions of the application have to run concurrently for some time.
 */
public class PrefixedKeyGenerator(gitProperties: GitProperties?, buildProperties: BuildProperties?) : KeyGenerator {
    public companion object {
        private const val KEY_SIZE: Int = 12
    }

    public val prefix: String = generatePrefix(gitProperties, buildProperties)

    override fun generate(target: Any, method: Method, vararg params: Any): PrefixedSimpleKey {
        return PrefixedSimpleKey(prefix, method.name, listOf(*params))
    }

    private fun generatePrefix(gitProperties: GitProperties?, buildProperties: BuildProperties?): String {
        return gitProperties?.shortCommitId ?: if (buildProperties?.time != null) DateTimeFormatter.ISO_INSTANT.format(
            buildProperties.time
        ) else buildProperties?.version ?: RandomStringUtils.randomAlphanumeric(KEY_SIZE)
    }
}
