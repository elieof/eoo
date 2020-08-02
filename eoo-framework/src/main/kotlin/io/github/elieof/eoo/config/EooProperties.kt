package io.github.elieof.eoo.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "eoo", ignoreUnknownFields = false)
class EooProperties {

    val async = Async()

    val http = Http()

    val logging = Logging()

    val metrics = Metrics()

    class Async {
        var corePoolSize = EooDefaults.Async.corePoolSize

        var maxPoolSize = EooDefaults.Async.maxPoolSize

        var queueCapacity = EooDefaults.Async.queueCapacity
    }

    class Http {

        val cache = Cache()

        class Cache {
            var timeToLiveInDays = EooDefaults.Http.Cache.timeToLiveInDays
        }
    }

    class Logging {

        var useJsonFormat = EooDefaults.Logging.useJsonFormat

        val logstash = Logstash()

        val file = File()

        val accessFile =
            File(EooDefaults.Logging.File.accessPrefix)

        class File(var prefix: String = EooDefaults.Logging.File.prefix) {
            var enabled = EooDefaults.Logging.File.enabled
            var dir = EooDefaults.Logging.File.dir
            var minIndex = EooDefaults.Logging.File.minIndex
            var maxIndex = EooDefaults.Logging.File.maxIndex
            var maxSize = EooDefaults.Logging.File.maxSize
            var maxHistory = EooDefaults.Logging.File.maxHistory
        }

        class Logstash {

            var enabled = EooDefaults.Logging.Logstash.enabled

            var host = EooDefaults.Logging.Logstash.host

            var port = EooDefaults.Logging.Logstash.port

            var queueSize = EooDefaults.Logging.Logstash.queueSize
        }
    }

    class Metrics {
        val logs = Logs()

        class Logs {
            var enabled = EooDefaults.Metrics.Logs.enabled
            var reportFrequency = EooDefaults.Metrics.Logs.reportFrequency
        }
    }
}
