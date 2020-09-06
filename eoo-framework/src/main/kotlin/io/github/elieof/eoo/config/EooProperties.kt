package io.github.elieof.eoo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "eoo", ignoreUnknownFields = false)
data class EooProperties(
    val async: Async = Async(),
    val http: Http = Http(),
    val logging: Logging = Logging(),
    val metrics: Metrics = Metrics(),
    val cache: Cache = Cache(),
    val mail: Mail = Mail(),
    val security: Security = Security(),
    val apiDocs: ApiDocs = ApiDocs(),
    val social: Social = Social(),
    val gateway: Gateway = Gateway(),
    val registry: Registry = Registry(),
    val clientApp: ClientApp = ClientApp(),
    val auditEvents: AuditEvents = AuditEvents()
) {

    data class Async(
        /** Async config core pool size. */
        val corePoolSize: Int = EooDefaults.Async.corePoolSize,
        /** Async config max pool size. */
        val maxPoolSize: Int = EooDefaults.Async.maxPoolSize,
        /** Async config queue capacity. */
        val queueCapacity: Int = EooDefaults.Async.queueCapacity
    )

    data class Http(val cache: Cache = Cache()) {
        data class Cache(val timeToLiveInDays: Int = EooDefaults.Http.Cache.timeToLiveInDays)
    }

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

    data class Cache(
        val hazelcast: Hazelcast = Hazelcast(),
        val caffeine: Caffeine = Caffeine(),
        val ehcache: Ehcache = Ehcache(),
        val infinispan: Infinispan = Infinispan(),
        val memCached: MemCached = MemCached(),
        val redis: Redis = Redis(),
    ) {
        data class Hazelcast(
            val timeToLiveSeconds: Long = EooDefaults.Cache.Hazelcast.timeToLiveSeconds,
            val backupCount: Int = EooDefaults.Cache.Hazelcast.backupCount
        )

        data class Caffeine(
            val timeToLiveSeconds: Long = EooDefaults.Cache.Caffeine.timeToLiveSeconds,
            val maxEntries: Long = EooDefaults.Cache.Caffeine.maxEntries
        )

        data class Ehcache(
            val timeToLiveSeconds: Long = EooDefaults.Cache.Ehcache.timeToLiveSeconds,
            val maxEntries: Long = EooDefaults.Cache.Ehcache.maxEntries
        )

        data class Infinispan(
            val configFile: String = EooDefaults.Cache.Infinispan.configFile,
            val statsEnabled: Boolean = EooDefaults.Cache.Infinispan.statsEnabled,
            val local: Local = Local(),
            val distributed: Distributed = Distributed(),
            val replicated: Replicated = Replicated(),
        ) {
            data class Local(
                val timeToLiveSeconds: Long = EooDefaults.Cache.Infinispan.Local.timeToLiveSeconds,
                val maxEntries: Long = EooDefaults.Cache.Infinispan.Local.maxEntries
            )

            data class Distributed(
                val timeToLiveSeconds: Long = EooDefaults.Cache.Infinispan.Distributed.timeToLiveSeconds,
                val maxEntries: Long = EooDefaults.Cache.Infinispan.Distributed.maxEntries,
                val instanceCount: Int = EooDefaults.Cache.Infinispan.Distributed.instanceCount
            )

            data class Replicated(
                val timeToLiveSeconds: Long = EooDefaults.Cache.Infinispan.Replicated.timeToLiveSeconds,
                val maxEntries: Long = EooDefaults.Cache.Infinispan.Replicated.maxEntries
            )
        }

        data class MemCached(
            val enabled: Boolean = EooDefaults.Cache.Memcached.enabled,
            val servers: String = EooDefaults.Cache.Memcached.servers,
            val expiration: Int = EooDefaults.Cache.Memcached.expiration,
            val useBinaryProtocol: Boolean = EooDefaults.Cache.Memcached.useBinaryProtocol,
            val authentication: Authentication = Authentication()
        ) {
            data class Authentication(
                val enabled: Boolean = EooDefaults.Cache.Memcached.Authentication.enabled,
                val username: String = EooDefaults.Cache.Memcached.Authentication.username,
                val password: String = EooDefaults.Cache.Memcached.Authentication.password
            )
        }

        data class Redis(
            val server: Array<String> = EooDefaults.Cache.Redis.server,
            val expiration: Int = EooDefaults.Cache.Redis.expiration,
            val cluster: Boolean = EooDefaults.Cache.Redis.cluster,
            val connectionPoolSize: Int = EooDefaults.Cache.Redis.connectionPoolSize,
            val connectionMinimumIdleSize: Int = EooDefaults.Cache.Redis.connectionMinimumIdleSize,
            val subscriptionConnectionPoolSize: Int = EooDefaults.Cache.Redis.subscriptionConnectionPoolSize,
            val subscriptionConnectionMinimumIdleSize: Int =
                EooDefaults.Cache.Redis.subscriptionConnectionMinimumIdleSize,
        )
    }

    data class Mail(
        val enabled: Boolean = EooDefaults.Mail.enabled,
        val from: String = EooDefaults.Mail.from,
        val baseUrl: String = EooDefaults.Mail.baseUrl
    )

    data class Security(
        val clientAuthorization: ClientAuthorization = ClientAuthorization(),
        val authentication: Authentication = Authentication(),
        val rememberMe: RememberMe = RememberMe(),
        val oauth2: Oauth2 = Oauth2()
    ) {
        data class ClientAuthorization(
            val accessTokenUri: String = EooDefaults.Security.ClientAuthorization.accessTokenUri,
            val tokenServiceId: String = EooDefaults.Security.ClientAuthorization.tokenServiceId,
            val clientId: String = EooDefaults.Security.ClientAuthorization.clientId,
            val clientSecret: String = EooDefaults.Security.ClientAuthorization.clientSecret,
        )

        data class Authentication(
            val jwt: Jwt = Jwt()
        ) {
            data class Jwt(
                val secret: String = EooDefaults.Security.Authentication.Jwt.secret,
                val base64Secret: String = EooDefaults.Security.Authentication.Jwt.base64Secret,
                val tokenValidityInSeconds: Long = EooDefaults.Security.Authentication.Jwt.tokenValidityInSeconds,
                val tokenValidityInSecondsForRememberMe: Long =
                    EooDefaults.Security.Authentication.Jwt.tokenValidityInSecondsForRememberMe,
            )
        }

        data class RememberMe(
            val key: String = EooDefaults.Security.RememberMe.key
        )

        data class Oauth2(
            val audience: Array<String> = EooDefaults.Security.Oauth2.audience
        )
    }

    data class ApiDocs(
        val title: String = EooDefaults.ApiDocs.title,
        val description: String = EooDefaults.ApiDocs.description,
        val version: String = EooDefaults.ApiDocs.version,
        val termsOfServiceUrl: String = EooDefaults.ApiDocs.termsOfServiceUrl,
        val contactName: String = EooDefaults.ApiDocs.contactName,
        val contactUrl: String = EooDefaults.ApiDocs.contactUrl,
        val contactEmail: String = EooDefaults.ApiDocs.contactEmail,
        val license: String = EooDefaults.ApiDocs.license,
        val licenseUrl: String = EooDefaults.ApiDocs.licenseUrl,
        val defaultIncludePattern: String = EooDefaults.ApiDocs.defaultIncludePattern,
        val host: String = EooDefaults.ApiDocs.host,
        val protocols: Array<String> = EooDefaults.ApiDocs.protocols,
        val servers: Array<String> = EooDefaults.ApiDocs.servers,
        val useDefaultResponseMessages: Boolean = EooDefaults.ApiDocs.useDefaultResponseMessages,
    )

    data class Social(
        val redirectAfterSignIn: String = EooDefaults.Social.redirectAfterSignIn
    )

    data class Gateway(
        val rateLimiting: RateLimiting = RateLimiting(),
        val authorizedMicroservicesEndpoints: Map<String, List<String>> =
            EooDefaults.Gateway.authorizedMicroservicesEndpoints
    ) {
        data class RateLimiting(
            val enabled: Boolean = EooDefaults.Gateway.RateLimiting.enabled,
            val limit: Long = EooDefaults.Gateway.RateLimiting.limit,
            val durationInSeconds: Int = EooDefaults.Gateway.RateLimiting.durationInSeconds,
        )
    }

    data class Registry(
        val password: String = EooDefaults.Registry.password
    )

    data class ClientApp(
        val name: String = EooDefaults.ClientApp.name
    )

    data class AuditEvents(
        val retentionPeriod: Int = EooDefaults.AuditEvents.retentionPeriod
    )
}
