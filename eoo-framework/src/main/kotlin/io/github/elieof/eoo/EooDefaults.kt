package io.github.elieof.eoo

interface EooDefaults {

    interface Async {
        companion object {
            const val corePoolSize = 2
            const val maxPoolSize = 50
            const val queueCapacity = 10000
        }
    }

    interface Http {
        interface Cache {
            companion object {
                const val timeToLiveInDays = 1461 // 4 years (including leap day)
            }
        }
    }

    interface Logging {
        interface Logstash {
            companion object {
                const val enabled = false
                const val host = "localhost"
                const val port = 5000
                const val queueSize = 512
            }
        }

        interface File {
            companion object {
                const val enabled = false
                const val dir = "./logs"
                const val prefix = "application"
                const val accessPrefix = "access"
                const val minIndex = 1
                const val maxIndex = 10
                const val maxSize = "10 MB" // "$value $unit" where unit is KB/MB/GB
                const val maxHistory = 1
            }
        }

        companion object {
            const val useJsonFormat = false
        }
    }

    interface Metrics {
        interface Jmx {
            companion object {
                const val enabled = false
            }
        }

        interface Logs {
            companion object {
                const val enabled = false
                const val reportFrequency: Long = 60
            }
        }

        interface Prometheus {
            companion object {
                const val enabled = false
                const val endpoint = "/prometheusMetrics"
            }
        }
    }
}
