package io.github.elieof.eoo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "eoo", ignoreUnknownFields = false)
data class EooProperties(
    val async: Async = Async(),
    val http: Http = Http(),
    val logging: Logging = Logging(),
    val metrics: Metrics = Metrics()
) {

    data class Async(
        /** Async config core pool size. */
        val corePoolSize: Int = EooDefaults.Async.corePoolSize,
        /** Async config max pool size. */
        val maxPoolSize: Int = EooDefaults.Async.maxPoolSize,
        /** Async config queue capacity. */
        val queueCapacity: Int = EooDefaults.Async.queueCapacity
    )

    data class Http(val cache: Cache = Cache())

    data class Cache(val timeToLiveInDays: Int = EooDefaults.Http.Cache.timeToLiveInDays)

    data class Logging(
        /** Use json format in logs. */
        val useJsonFormat: Boolean = EooDefaults.Logging.useJsonFormat,
        val logstash: Logstash = Logstash(),
        val file: AppFile = AppFile(),
        val accessFile: AccessFile = AccessFile()
    ) {

        open class File(
            open val prefix: String = EooDefaults.Logging.File.prefix,
            open val enabled: Boolean = EooDefaults.Logging.File.enabled,
            open val dir: String = EooDefaults.Logging.File.dir
        )

        data class AppFile(
            /** File prefix. */
            override val prefix: String = EooDefaults.Logging.File.prefix,
            /** Enable logging to a file. */
            override val enabled: Boolean = EooDefaults.Logging.File.enabled,
            /** Logging file directory. */
            override val dir: String = EooDefaults.Logging.File.dir,
            /** Logging file min index. */
            val minIndex: Int = EooDefaults.Logging.File.minIndex,
            /** Logging file max index. */
            val maxIndex: Int = EooDefaults.Logging.File.maxIndex,
            /** Logging file max size in KB, MB or GB ('10 MB'). */
            val maxSize: String = EooDefaults.Logging.File.maxSize
        ) : File(prefix, enabled, dir)

        data class AccessFile(
            /** Access File prefix. */
            override val prefix: String = EooDefaults.Logging.File.accessPrefix,
            /** Enable logging of http requests to a file. */
            override val enabled: Boolean = EooDefaults.Logging.File.enabled,
            /** Access logging file directory. */
            override val dir: String = EooDefaults.Logging.File.dir,
            /** Access logging file max history in days. */
            val maxHistory: Int = EooDefaults.Logging.File.maxHistory
        ) : File(prefix, enabled, dir)

        data class Logstash(
            /** Enable logstash configuration. */
            val enabled: Boolean = EooDefaults.Logging.Logstash.enabled,
            /** Logstash host. */
            val host: String = EooDefaults.Logging.Logstash.host,
            /** Logstash port. */
            val port: Int = EooDefaults.Logging.Logstash.port,
            /** Logstash queue size. */
            val queueSize: Int = EooDefaults.Logging.Logstash.queueSize
        )
    }

    data class Metrics(val logs: Logs = Logs()) {

        data class Logs(
            /** Enable metrics logs. */
            val enabled: Boolean = EooDefaults.Metrics.Logs.enabled,
            /** Metrics logs frequency report in seconds. */
            val frequencyReport: Long = EooDefaults.Metrics.Logs.frequencyReport
        )
    }
}
