package io.github.elieof.eoo.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EooPropertiesTest {

    private lateinit var properties: EooProperties

    @BeforeEach
    fun setUp() {
        properties = EooProperties()
    }

    @Test
    @Throws(Exception::class)
    fun testComplete() {
        // Slightly pedantic; this checks if there are tests for each of the properties.
        val set: MutableSet<String> = LinkedHashSet(64, 1f)
        reflect(properties, set, "test")
        for (name in set) {
            assertThat(this.javaClass.getDeclaredMethod(name)).isNotNull
        }
    }

    @Test
    fun testAsyncCorePoolSize() {
        val obj = properties.async
        var value = EooDefaults.Async.corePoolSize
        assertThat(obj.corePoolSize).isEqualTo(value)
        value++
        obj.corePoolSize = value
        assertThat(obj.corePoolSize).isEqualTo(value)
    }

    @Test
    fun testAsyncMaxPoolSize() {
        val obj = properties.async
        var value = EooDefaults.Async.maxPoolSize
        assertThat(obj.maxPoolSize).isEqualTo(value)
        value++
        obj.maxPoolSize = value
        assertThat(obj.maxPoolSize).isEqualTo(value)
    }

    @Test
    fun testAsyncQueueCapacity() {
        val obj = properties.async
        var value = EooDefaults.Async.queueCapacity
        assertThat(obj.queueCapacity).isEqualTo(value)
        value++
        obj.queueCapacity = value
        assertThat(obj.queueCapacity).isEqualTo(value)
    }

    @Test
    fun testHttpCacheTimeToLiveInDays() {
        val obj = properties.http.cache
        var value = EooDefaults.Http.Cache.timeToLiveInDays
        assertThat(obj.timeToLiveInDays).isEqualTo(value)
        value++
        obj.timeToLiveInDays = value
        assertThat(obj.timeToLiveInDays).isEqualTo(value)
    }

    @Test
    fun testLoggingUseJsonFormat() {
        val obj = properties.logging
        var value = EooDefaults.Logging.useJsonFormat
        assertThat(obj.useJsonFormat).isEqualTo(value)
        value = true
        obj.useJsonFormat = value
        assertThat(obj.useJsonFormat).isEqualTo(value)
    }

    @Test
    fun testLoggingLogstashEnabled() {
        val obj = properties.logging.logstash
        var value = EooDefaults.Logging.Logstash.enabled
        assertThat(obj.enabled).isEqualTo(value)
        value = !value
        obj.enabled = value
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testLoggingLogstashHost() {
        val obj = properties.logging.logstash
        var value = EooDefaults.Logging.Logstash.host
        assertThat(obj.host).isEqualTo(value)
        value = "1$value"
        obj.host = value
        assertThat(obj.host).isEqualTo(value)
    }

    @Test
    fun testLoggingLogstashPort() {
        val obj = properties.logging.logstash
        var value = EooDefaults.Logging.Logstash.port
        assertThat(obj.port).isEqualTo(value)
        value++
        obj.port = value
        assertThat(obj.port).isEqualTo(value)
    }

    @Test
    fun testLoggingLogstashQueueSize() {
        val obj = properties.logging.logstash
        var value = EooDefaults.Logging.Logstash.queueSize
        assertThat(obj.queueSize).isEqualTo(value)
        value++
        obj.queueSize = value
        assertThat(obj.queueSize).isEqualTo(value)
    }

    @Test
    fun testLoggingFileEnabled() {
        val obj = properties.logging.file
        var value = EooDefaults.Logging.File.enabled
        assertThat(obj.enabled).isEqualTo(value)
        value = !value
        obj.enabled = value
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testLoggingFilePrefix() {
        val obj = properties.logging.file
        var value = EooDefaults.Logging.File.prefix
        assertThat(obj.prefix).isEqualTo(value)
        value = "1$value"
        obj.prefix = value
        assertThat(obj.prefix).isEqualTo(value)
    }

    @Test
    fun testLoggingFileDir() {
        val obj = properties.logging.file
        var value = EooDefaults.Logging.File.dir
        assertThat(obj.dir).isEqualTo(value)
        value = "1$value"
        obj.dir = value
        assertThat(obj.dir).isEqualTo(value)
    }

    @Test
    fun testLoggingFileMinIndex() {
        val obj = properties.logging.file
        var value = EooDefaults.Logging.File.minIndex
        assertThat(obj.minIndex).isEqualTo(value)
        value++
        obj.minIndex = value
        assertThat(obj.minIndex).isEqualTo(value)
    }

    @Test
    fun testLoggingFileMaxIndex() {
        val obj = properties.logging.file
        var value = EooDefaults.Logging.File.maxIndex
        assertThat(obj.maxIndex).isEqualTo(value)
        value++
        obj.maxIndex = value
        assertThat(obj.maxIndex).isEqualTo(value)
    }

    @Test
    fun testLoggingFileMaxSize() {
        val obj = properties.logging.file
        var value = EooDefaults.Logging.File.maxSize
        assertThat(obj.maxSize).isEqualTo(value)
        value = "1$value"
        obj.maxSize = value
        assertThat(obj.maxSize).isEqualTo(value)
    }

    @Test
    fun testLoggingFileMaxHistory() {
        val obj = properties.logging.file
        var value = EooDefaults.Logging.File.maxHistory
        assertThat(obj.maxHistory).isEqualTo(value)
        value++
        obj.maxHistory = value
        assertThat(obj.maxHistory).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileEnabled() {
        val obj = properties.logging.accessFile
        var value = EooDefaults.Logging.File.enabled
        assertThat(obj.enabled).isEqualTo(value)
        value = !value
        obj.enabled = value
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFilePrefix() {
        val obj = properties.logging.accessFile
        var value = EooDefaults.Logging.File.accessPrefix
        assertThat(obj.prefix).isEqualTo(value)
        value = "1$value"
        obj.prefix = value
        assertThat(obj.prefix).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileDir() {
        val obj = properties.logging.accessFile
        var value = EooDefaults.Logging.File.dir
        assertThat(obj.dir).isEqualTo(value)
        value = "1$value"
        obj.dir = value
        assertThat(obj.dir).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileMinIndex() {
        val obj = properties.logging.accessFile
        var value = EooDefaults.Logging.File.minIndex
        assertThat(obj.minIndex).isEqualTo(value)
        value++
        obj.minIndex = value
        assertThat(obj.minIndex).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileMaxIndex() {
        val obj = properties.logging.accessFile
        var value = EooDefaults.Logging.File.maxIndex
        assertThat(obj.maxIndex).isEqualTo(value)
        value++
        obj.maxIndex = value
        assertThat(obj.maxIndex).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileMaxSize() {
        val obj = properties.logging.accessFile
        var value = EooDefaults.Logging.File.maxSize
        assertThat(obj.maxSize).isEqualTo(value)
        value = "1$value"
        obj.maxSize = value
        assertThat(obj.maxSize).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileMaxHistory() {
        val obj = properties.logging.accessFile
        var value = EooDefaults.Logging.File.maxHistory
        assertThat(obj.maxHistory).isEqualTo(value)
        value++
        obj.maxHistory = value
        assertThat(obj.maxHistory).isEqualTo(value)
    }

    @Test
    fun testMetricsLogsEnabled() {
        val obj = properties.metrics.logs
        var value = EooDefaults.Metrics.Logs.enabled
        assertThat(obj.enabled).isEqualTo(value)
        value = !value
        obj.enabled = value
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testMetricsLogsReportFrequency() {
        val obj = properties.metrics.logs
        var value = EooDefaults.Metrics.Logs.reportFrequency
        assertThat(obj.reportFrequency).isEqualTo(value)
        value++
        obj.reportFrequency = value
        assertThat(obj.reportFrequency).isEqualTo(value)
    }

    @Throws(java.lang.Exception::class)
    private fun reflect(obj: Any, dst: MutableSet<String>, prefix: String) {
        val src: Class<*> = obj.javaClass
        for (method in src.declaredMethods) {
            val name = method.name
            if (name.startsWith("get")) {
                val res = method.invoke(obj, *method.parameters)
                if (res != null && src == res.javaClass.declaringClass) {
                    reflect(res, dst, prefix + name.substring(3))
                }
            } else if (name.startsWith("set")) {
                dst.add(prefix + name.substring(3))
            }
        }
    }
}
