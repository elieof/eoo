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
        assertThat(EooDefaults).isNotNull
        assertThat(EooDefaults.Async).isNotNull
        val value = EooDefaults.Async.corePoolSize
        assertThat(obj.corePoolSize).isEqualTo(value)
    }

    @Test
    fun testAsyncMaxPoolSize() {
        val obj = properties.async
        val value = EooDefaults.Async.maxPoolSize
        assertThat(obj.maxPoolSize).isEqualTo(value)
    }

    @Test
    fun testAsyncQueueCapacity() {
        val obj = properties.async
        val value = EooDefaults.Async.queueCapacity
        assertThat(obj.queueCapacity).isEqualTo(value)
    }

    @Test
    fun testHttpCacheTimeToLiveInDays() {
        val obj = properties.http.cache
        assertThat(EooDefaults.Http).isNotNull
        assertThat(EooDefaults.Http.Cache).isNotNull
        val value = EooDefaults.Http.Cache.timeToLiveInDays
        assertThat(obj.timeToLiveInDays).isEqualTo(value)
    }

    @Test
    fun testLoggingUseJsonFormat() {
        val obj = properties.logging
        assertThat(EooDefaults.Logging).isNotNull
        val value = EooDefaults.Logging.useJsonFormat
        assertThat(obj.useJsonFormat).isEqualTo(value)
    }

    @Test
    fun testLoggingLogstashEnabled() {
        val obj = properties.logging.logstash
        assertThat(EooDefaults.Logging.Logstash).isNotNull

        val value = EooDefaults.Logging.Logstash.enabled
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testLoggingLogstashHost() {
        val obj = properties.logging.logstash
        val value = EooDefaults.Logging.Logstash.host
        assertThat(obj.host).isEqualTo(value)
    }

    @Test
    fun testLoggingLogstashPort() {
        val obj = properties.logging.logstash
        val value = EooDefaults.Logging.Logstash.port
        assertThat(obj.port).isEqualTo(value)
    }

    @Test
    fun testLoggingLogstashQueueSize() {
        val obj = properties.logging.logstash
        val value = EooDefaults.Logging.Logstash.queueSize
        assertThat(obj.queueSize).isEqualTo(value)
    }

    @Test
    fun testLoggingFileEnabled() {
        val obj = properties.logging.file
        assertThat(EooDefaults.Logging.File).isNotNull
        val value = EooDefaults.Logging.File.enabled
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testFilPrefix() {
        val obj = EooProperties.Logging.File()
        val value = EooDefaults.Logging.File.prefix
        assertThat(obj.prefix).isEqualTo(value)
    }

    @Test
    fun testFileEnabled() {
        val obj = EooProperties.Logging.File()
        val value = EooDefaults.Logging.File.enabled
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testFileDir() {
        val obj = EooProperties.Logging.File()
        val value = EooDefaults.Logging.File.dir
        assertThat(obj.dir).isEqualTo(value)
    }

    @Test
    fun testLoggingFilePrefix() {
        val obj = properties.logging.file
        val value = EooDefaults.Logging.File.prefix
        assertThat(obj.prefix).isEqualTo(value)
    }

    @Test
    fun testLoggingFileDir() {
        val obj = properties.logging.file
        val value = EooDefaults.Logging.File.dir
        assertThat(obj.dir).isEqualTo(value)
    }

    @Test
    fun testLoggingFileMinIndex() {
        val obj = properties.logging.file
        val value = EooDefaults.Logging.File.minIndex
        assertThat(obj.minIndex).isEqualTo(value)
    }

    @Test
    fun testLoggingFileMaxIndex() {
        val obj = properties.logging.file
        val value = EooDefaults.Logging.File.maxIndex
        assertThat(obj.maxIndex).isEqualTo(value)
    }

    @Test
    fun testLoggingFileMaxSize() {
        val obj = properties.logging.file
        val value = EooDefaults.Logging.File.maxSize
        assertThat(obj.maxSize).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileEnabled() {
        val obj = properties.logging.accessFile
        val value = EooDefaults.Logging.File.enabled
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFilePrefix() {
        val obj = properties.logging.accessFile
        val value = EooDefaults.Logging.File.accessPrefix
        assertThat(obj.prefix).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileDir() {
        val obj = properties.logging.accessFile
        val value = EooDefaults.Logging.File.dir
        assertThat(obj.dir).isEqualTo(value)
    }

    @Test
    fun testLoggingAccessFileMaxHistory() {
        val obj = properties.logging.accessFile
        val value = EooDefaults.Logging.File.maxHistory
        assertThat(obj.maxHistory).isEqualTo(value)
    }

    @Test
    fun testCacheHazelcastTimeToLiveSeconds() {
        val obj = properties.cache.hazelcast
        assertThat(EooDefaults.Cache).isNotNull
        assertThat(EooDefaults.Cache.Hazelcast).isNotNull
        val value = EooDefaults.Cache.Hazelcast.timeToLiveSeconds
        assertThat(obj.timeToLiveSeconds).isEqualTo(value)
    }

    @Test
    fun testCacheHazelcastBackupCount() {
        val obj = properties.cache.hazelcast
        val value = EooDefaults.Cache.Hazelcast.backupCount
        assertThat(obj.backupCount).isEqualTo(value)
    }

    @Test
    fun testCacheCaffeineTimeToLiveSeconds() {
        val obj = properties.cache.caffeine
        assertThat(EooDefaults.Cache.Caffeine).isNotNull
        val value = EooDefaults.Cache.Caffeine.timeToLiveSeconds
        assertThat(obj.timeToLiveSeconds).isEqualTo(value)
    }

    @Test
    fun testCacheCaffeineMaxEntries() {
        val obj = properties.cache.caffeine
        val value = EooDefaults.Cache.Caffeine.maxEntries
        assertThat(obj.maxEntries).isEqualTo(value)
    }

    @Test
    fun testCacheEhcacheTimeToLiveSeconds() {
        val obj = properties.cache.ehcache
        assertThat(EooDefaults.Cache.Ehcache).isNotNull
        val value = EooDefaults.Cache.Ehcache.timeToLiveSeconds
        assertThat(obj.timeToLiveSeconds).isEqualTo(value)
    }

    @Test
    fun testCacheEhcacheMaxEntries() {
        val obj = properties.cache.ehcache
        val value = EooDefaults.Cache.Ehcache.maxEntries
        assertThat(obj.maxEntries).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanConfigFile() {
        val obj = properties.cache.infinispan
        assertThat(EooDefaults.Cache.Infinispan).isNotNull
        val value = EooDefaults.Cache.Infinispan.configFile
        assertThat(obj.configFile).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanStatsEnabled() {
        val obj = properties.cache.infinispan
        val value = EooDefaults.Cache.Infinispan.statsEnabled
        assertThat(obj.statsEnabled).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanLocalTimeToLiveSeconds() {
        val obj = properties.cache.infinispan.local
        assertThat(EooDefaults.Cache.Infinispan.Local).isNotNull
        val value = EooDefaults.Cache.Infinispan.Local.timeToLiveSeconds
        assertThat(obj.timeToLiveSeconds).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanLocalMaxEntries() {
        val obj = properties.cache.infinispan.local
        val value = EooDefaults.Cache.Infinispan.Local.maxEntries
        assertThat(obj.maxEntries).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanDistributedTimeToLiveSeconds() {
        val obj = properties.cache.infinispan.distributed
        assertThat(EooDefaults.Cache.Infinispan.Distributed).isNotNull
        val value = EooDefaults.Cache.Infinispan.Distributed.timeToLiveSeconds
        assertThat(obj.timeToLiveSeconds).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanDistributedMaxEntries() {
        val obj = properties.cache.infinispan.distributed
        val value = EooDefaults.Cache.Infinispan.Distributed.maxEntries
        assertThat(obj.maxEntries).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanDistributedInstanceCount() {
        val obj = properties.cache.infinispan.distributed
        val value = EooDefaults.Cache.Infinispan.Distributed.instanceCount
        assertThat(obj.instanceCount).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanReplicatedTimeToLiveSeconds() {
        val obj = properties.cache.infinispan.replicated
        assertThat(EooDefaults.Cache.Infinispan.Replicated).isNotNull
        val value = EooDefaults.Cache.Infinispan.Replicated.timeToLiveSeconds
        assertThat(obj.timeToLiveSeconds).isEqualTo(value)
    }

    @Test
    fun testCacheInfinispanReplicatedMaxEntries() {
        val obj = properties.cache.infinispan.replicated
        val value = EooDefaults.Cache.Infinispan.Replicated.maxEntries
        assertThat(obj.maxEntries).isEqualTo(value)
    }

    @Test
    fun testCacheMemCachedEnabled() {
        val obj = properties.cache.memCached
        assertThat(EooDefaults.Cache.Memcached).isNotNull
        val value = EooDefaults.Cache.Memcached.enabled
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testCacheMemCachedServers() {
        val obj = properties.cache.memCached
        val value = EooDefaults.Cache.Memcached.servers
        assertThat(obj.servers).isEqualTo(value)
    }

    @Test
    fun testCacheMemCachedExpiration() {
        val obj = properties.cache.memCached
        val value = EooDefaults.Cache.Memcached.expiration
        assertThat(obj.expiration).isEqualTo(value)
    }

    @Test
    fun testCacheMemCachedUseBinaryProtocol() {
        val obj = properties.cache.memCached
        val value = EooDefaults.Cache.Memcached.useBinaryProtocol
        assertThat(obj.useBinaryProtocol).isEqualTo(value)
    }

    @Test
    fun testCacheMemCachedAuthenticationEnabled() {
        val obj = properties.cache.memCached.authentication
        assertThat(EooDefaults.Cache.Memcached.Authentication).isNotNull
        val value = EooDefaults.Cache.Memcached.Authentication.enabled
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testCacheMemCachedAuthenticationUsername() {
        val obj = properties.cache.memCached.authentication
        val value = EooDefaults.Cache.Memcached.Authentication.username
        assertThat(obj.username).isEqualTo(value)
    }

    @Test
    fun testCacheMemCachedAuthenticationPassword() {
        val obj = properties.cache.memCached.authentication
        val value = EooDefaults.Cache.Memcached.Authentication.password
        assertThat(obj.password).isEqualTo(value)
    }

    @Test
    fun testCacheRedisServer() {
        val obj = properties.cache.redis
        assertThat(EooDefaults.Cache.Redis).isNotNull
        val value = EooDefaults.Cache.Redis.server
        assertThat(obj.server).isEqualTo(value)
    }

    @Test
    fun testCacheRedisExpiration() {
        val obj = properties.cache.redis
        val value = EooDefaults.Cache.Redis.expiration
        assertThat(obj.expiration).isEqualTo(value)
    }

    @Test
    fun testCacheRedisCluster() {
        val obj = properties.cache.redis
        val value = EooDefaults.Cache.Redis.cluster
        assertThat(obj.cluster).isEqualTo(value)
    }

    @Test
    fun testCacheRedisConnectionPoolSize() {
        val obj = properties.cache.redis
        val value = EooDefaults.Cache.Redis.connectionPoolSize
        assertThat(obj.connectionPoolSize).isEqualTo(value)
    }

    @Test
    fun testCacheRedisConnectionMinimumIdleSize() {
        val obj = properties.cache.redis
        val value = EooDefaults.Cache.Redis.connectionMinimumIdleSize
        assertThat(obj.connectionMinimumIdleSize).isEqualTo(value)
    }

    @Test
    fun testCacheRedisSubscriptionConnectionPoolSize() {
        val obj = properties.cache.redis
        val value = EooDefaults.Cache.Redis.subscriptionConnectionPoolSize
        assertThat(obj.subscriptionConnectionPoolSize).isEqualTo(value)
    }

    @Test
    fun testCacheRedisSubscriptionConnectionMinimumIdleSize() {
        val obj = properties.cache.redis
        val value = EooDefaults.Cache.Redis.subscriptionConnectionMinimumIdleSize
        assertThat(obj.subscriptionConnectionMinimumIdleSize).isEqualTo(value)
    }

    @Test
    fun testMailEnabled() {
        val obj = properties.mail
        assertThat(EooDefaults.Mail).isNotNull
        val value = EooDefaults.Mail.enabled
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testMailFrom() {
        val obj = properties.mail
        val value = EooDefaults.Mail.from
        assertThat(obj.from).isEqualTo(value)
    }

    @Test
    fun testMailBaseUrl() {
        val obj = properties.mail
        val value = EooDefaults.Mail.baseUrl
        assertThat(obj.baseUrl).isEqualTo(value)
    }

    @Test
    fun testSecurityClientAuthorizationAccessTokenUri() {
        val obj = properties.security.clientAuthorization
        assertThat(EooDefaults.Security).isNotNull
        assertThat(EooDefaults.Security.ClientAuthorization).isNotNull
        val value = EooDefaults.Security.ClientAuthorization.accessTokenUri
        assertThat(obj.accessTokenUri).isEqualTo(value)
    }

    @Test
    fun testSecurityClientAuthorizationTokenServiceId() {
        val obj = properties.security.clientAuthorization
        val value = EooDefaults.Security.ClientAuthorization.tokenServiceId
        assertThat(obj.tokenServiceId).isEqualTo(value)
    }

    @Test
    fun testSecurityClientAuthorizationClientId() {
        val obj = properties.security.clientAuthorization
        val value = EooDefaults.Security.ClientAuthorization.clientId
        assertThat(obj.clientId).isEqualTo(value)
    }

    @Test
    fun testSecurityClientAuthorizationClientSecret() {
        val obj = properties.security.clientAuthorization
        val value = EooDefaults.Security.ClientAuthorization.clientSecret
        assertThat(obj.clientSecret).isEqualTo(value)
    }

    @Test
    fun testSecurityAuthenticationJwtSecret() {
        val obj = properties.security.authentication.jwt
        assertThat(EooDefaults.Security.Authentication).isNotNull
        assertThat(EooDefaults.Security.Authentication.Jwt).isNotNull
        val value = EooDefaults.Security.Authentication.Jwt.secret
        assertThat(obj.secret).isEqualTo(value)
    }

    @Test
    fun testSecurityAuthenticationJwtBase64Secret() {
        val obj = properties.security.authentication.jwt
        val value = EooDefaults.Security.Authentication.Jwt.base64Secret
        assertThat(obj.base64Secret).isEqualTo(value)
    }

    @Test
    fun testSecurityAuthenticationJwtTokenValidityInSeconds() {
        val obj = properties.security.authentication.jwt
        val value = EooDefaults.Security.Authentication.Jwt.tokenValidityInSeconds
        assertThat(obj.tokenValidityInSeconds).isEqualTo(value)
    }

    @Test
    fun testSecurityAuthenticationJwtTokenValidityInSecondsForRememberMe() {
        val obj = properties.security.authentication.jwt
        val value = EooDefaults.Security.Authentication.Jwt.tokenValidityInSecondsForRememberMe
        assertThat(obj.tokenValidityInSecondsForRememberMe).isEqualTo(value)
    }

    @Test
    fun testSecurityRememberMeKey() {
        val obj = properties.security.rememberMe
        assertThat(EooDefaults.Security.RememberMe).isNotNull
        val value = EooDefaults.Security.RememberMe.key
        assertThat(obj.key).isEqualTo(value)
    }

    @Test
    fun testSecurityOauth2() {
        val obj = properties.security.oauth2
        assertThat(EooDefaults.Security.Oauth2).isNotNull
        val value = EooDefaults.Security.Oauth2.audience
        assertThat(obj.audience).isEqualTo(value)
    }

    @Test
    fun testApiDocsTitle() {
        val obj = properties.apiDocs
        assertThat(EooDefaults.ApiDocs).isNotNull
        val value = EooDefaults.ApiDocs.title
        assertThat(obj.title).isEqualTo(value)
    }

    @Test
    fun testApiDocsDescription() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.description
        assertThat(obj.description).isEqualTo(value)
    }

    @Test
    fun testApiDocsVersion() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.version
        assertThat(obj.version).isEqualTo(value)
    }

    @Test
    fun testApiDocsTermsOfServiceUrl() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.termsOfServiceUrl
        assertThat(obj.termsOfServiceUrl).isEqualTo(value)
    }

    @Test
    fun testApiDocsContactName() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.contactName
        assertThat(obj.contactName).isEqualTo(value)
    }

    @Test
    fun testApiDocsContactUrl() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.contactUrl
        assertThat(obj.contactUrl).isEqualTo(value)
    }

    @Test
    fun testApiDocsContactEmail() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.contactEmail
        assertThat(obj.contactEmail).isEqualTo(value)
    }

    @Test
    fun testApiDocsLicense() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.license
        assertThat(obj.license).isEqualTo(value)
    }

    @Test
    fun testApiDocsLicenseUrl() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.licenseUrl
        assertThat(obj.licenseUrl).isEqualTo(value)
    }

    @Test
    fun testApiDocsDefaultIncludePattern() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.defaultIncludePattern
        assertThat(obj.defaultIncludePattern).isEqualTo(value)
    }

    @Test
    fun testApiDocsHost() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.host
        assertThat(obj.host).isEqualTo(value)
    }

    @Test
    fun testApiDocsProtocols() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.protocols
        assertThat(obj.protocols).isEqualTo(value)
    }

    @Test
    fun testServerUrl() {
        val obj = EooProperties.ApiDocs.Server()
        val value = EooDefaults.ApiDocs.Server.url
        assertThat(obj.url).isEqualTo(value)
    }


    @Test
    fun testServerDescription() {
        val obj = EooProperties.ApiDocs.Server()
        val value = EooDefaults.ApiDocs.Server.description
        assertThat(obj.description).isEqualTo(value)
    }

    @Test
    fun testServerName() {
        val obj = EooProperties.ApiDocs.Server()
        val value = EooDefaults.ApiDocs.Server.name
        assertThat(obj.name).isEqualTo(value)
    }

    @Test
    fun testApiDocsServers() {
        val obj = properties.apiDocs
        assertThat(EooDefaults.ApiDocs.Server).isNotNull
        val value = EooDefaults.ApiDocs.servers
        assertThat(obj.servers).isEqualTo(value)
    }

    @Test
    fun testApiDocsUseDefaultResponseMessages() {
        val obj = properties.apiDocs
        val value = EooDefaults.ApiDocs.useDefaultResponseMessages
        assertThat(obj.useDefaultResponseMessages).isEqualTo(value)
    }

    @Test
    fun testSocialRedirectAfterSignIng() {
        val obj = properties.social
        assertThat(EooDefaults.Social).isNotNull
        val value = EooDefaults.Social.redirectAfterSignIn
        assertThat(obj.redirectAfterSignIn).isEqualTo(value)
    }

    @Test
    fun testGatewayRateLimitingEnabled() {
        val obj = properties.gateway.rateLimiting
        assertThat(EooDefaults.Gateway).isNotNull
        assertThat(EooDefaults.Gateway.RateLimiting).isNotNull
        val value = EooDefaults.Gateway.RateLimiting.enabled
        assertThat(obj.enabled).isEqualTo(value)
    }

    @Test
    fun testGatewayRateLimitingLimit() {
        val obj = properties.gateway.rateLimiting
        val value = EooDefaults.Gateway.RateLimiting.limit
        assertThat(obj.limit).isEqualTo(value)
    }

    @Test
    fun testGatewayRateLimitingDurationInSeconds() {
        val obj = properties.gateway.rateLimiting
        val value = EooDefaults.Gateway.RateLimiting.durationInSeconds
        assertThat(obj.durationInSeconds).isEqualTo(value)
    }

    @Test
    fun testGatewayAuthorizedMicroservicesEndpoints() {
        val obj = properties.gateway
        val value = EooDefaults.Gateway.authorizedMicroservicesEndpoints
        assertThat(obj.authorizedMicroservicesEndpoints).isEqualTo(value)
    }

    @Test
    fun testRegistryPassword() {
        val obj = properties.registry
        assertThat(EooDefaults.Registry).isNotNull
        val value = EooDefaults.Registry.password
        assertThat(obj.password).isEqualTo(value)
    }

    @Test
    fun testClientAppName() {
        val obj = properties.clientApp
        assertThat(EooDefaults.ClientApp).isNotNull
        val value = EooDefaults.ClientApp.name
        assertThat(obj.name).isEqualTo(value)
    }

    @Test
    fun testAuditEventsRetentionPeriod() {
        val obj = properties.auditEvents
        assertThat(EooDefaults.AuditEvents).isNotNull
        val value = EooDefaults.AuditEvents.retentionPeriod
        assertThat(obj.retentionPeriod).isEqualTo(value)
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
