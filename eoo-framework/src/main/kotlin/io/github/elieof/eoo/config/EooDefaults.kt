package io.github.elieof.eoo.config

import java.util.*

/**
 * Default values for [EooProperties]
 */
object EooDefaults {

    object Async {
        const val corePoolSize = 2
        const val maxPoolSize = 50
        const val queueCapacity = 10000
    }

    object Http {
        object Cache {
            const val timeToLiveInDays = 1461 // 4 years (including leap day)
        }
    }

    object Logging {
        const val useJsonFormat = false

        object Logstash {
            const val enabled = false
            const val host = "localhost"
            const val port = 5000
            const val queueSize = 512
        }

        object File {
            const val enabled = false
            const val dir = ""
            const val prefix = "application"
            const val accessPrefix = "access"
            const val minIndex = 1
            const val maxIndex = 10
            const val maxSize = "10 MB" // "$value $unit" where unit is KB/MB/GB
            const val maxHistory = 1
        }
    }

    object Metrics {
        object Jmx {
            const val enabled = false
        }

        object Logs {
            const val enabled = false
            const val frequencyReport: Long = 60
        }
    }

    object Cache {
        object Hazelcast {
            const val timeToLiveSeconds: Long = 3600
            const val backupCount = 1
        }

        object Caffeine {
            const val timeToLiveSeconds: Long = 3600 // 1 hour
            const val maxEntries: Long = 100
        }

        object Ehcache {
            const val timeToLiveSeconds: Long = 3600 // 1 hour
            const val maxEntries: Long = 100
        }

        object Infinispan {
            const val configFile = "default-configs/default-jgroups-tcp.xml"
            const val statsEnabled = false

            object Local {
                const val timeToLiveSeconds: Long = 60 // 1 minute
                const val maxEntries: Long = 100
            }

            object Distributed {
                const val timeToLiveSeconds: Long = 60 // 1 minute
                const val maxEntries: Long = 100
                const val instanceCount = 1
            }

            object Replicated {
                const val timeToLiveSeconds: Long = 60 // 1 minute
                const val maxEntries: Long = 100
            }
        }

        object Memcached {
            const val enabled = false
            const val servers = "localhost:11211"
            const val expiration = 300 // 5 minutes
            const val useBinaryProtocol = true

            object Authentication {
                const val enabled = false
                const val username = "username"
                const val password = ""
            }
        }

        object Redis {
            val server = arrayOf("redis://localhost:6379")
            const val expiration = 300 // 5 minutes
            const val cluster = false
            const val connectionPoolSize = 64 // default as in redisson
            const val connectionMinimumIdleSize = 24 // default as in redisson
            const val subscriptionConnectionPoolSize = 50 // default as in redisson
            const val subscriptionConnectionMinimumIdleSize = 1 // default as in redisson
        }
    }

    object Mail {
        const val enabled = false
        const val from = ""
        const val baseUrl = ""
    }

    object Security {
        object ClientAuthorization {
            const val accessTokenUri = ""
            const val tokenServiceId = ""
            const val clientId = ""
            const val clientSecret = ""
        }

        object Authentication {
            object Jwt {
                const val secret = "secret"
                const val base64Secret = "base64Secret"
                const val tokenValidityInSeconds: Long = 1800 // 30 minutes
                const val tokenValidityInSecondsForRememberMe: Long = 2592000 // 30 days
            }
        }

        object RememberMe {
            const val key = "rememberMe"
        }

        object Oauth2 {
            val audience = emptyArray<String>()
        }
    }

    object ApiDocs {
        const val title = "Application API"
        const val description = "API documentation"
        const val version = "0.0.1"
        const val termsOfServiceUrl = ""
        const val contactName = ""
        const val contactUrl = ""
        const val contactEmail = ""
        const val license = ""
        const val licenseUrl = ""
        const val defaultIncludePattern = "/api/.*"
        const val host = ""
        val protocols = emptyArray<String>()
        val servers = emptyArray<EooProperties.ApiDocs.Server>()
        const val useDefaultResponseMessages = true

        object Server {
            const val name = ""
            const val url = ""
            const val description = ""
        }
    }

    object Social {
        const val redirectAfterSignIn = "/#/home"
    }

    object Gateway {
        val authorizedMicroservicesEndpoints: Map<String, List<String>> = LinkedHashMap()

        object RateLimiting {
            const val enabled = false
            const val limit = 100000L
            const val durationInSeconds = 3600
        }
    }

    object Ribbon {
        val displayOnActiveProfiles = emptyArray<String>()
    }

    object Registry {
        const val password = ""
    }

    object ClientApp {
        const val name = "eooApp"
    }

    object AuditEvents {
        const val retentionPeriod = 30
    }
}
