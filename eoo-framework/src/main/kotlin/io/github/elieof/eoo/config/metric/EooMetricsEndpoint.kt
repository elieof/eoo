package io.github.elieof.eoo.config.metric

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.FunctionCounter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.TimeGauge
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.search.Search
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@WebEndpoint(id = "eoometrics")
class EooMetricsEndpoint(private val meterRegistry: MeterRegistry) {

    companion object {
        private val logger = LoggerFactory.getLogger(EooMetricsEndpoint::class.java)

        /** Constant `MISSING_NAME_TAG_MESSAGE="Missing name tag for metric {}"`  */
        const val MISSING_NAME_TAG_MESSAGE = "Missing name tag for metric {}"
    }

    /**
     * GET /management/eoo-metrics
     *
     *
     * Give metrics displayed on Metrics page
     *
     * @return a Map with a String defining a category of metrics as Key and
     * another Map containing metrics related to this category as Value
     */
    @ReadOperation
    fun allMetrics(): Map<String, Map<String, Any>> {
        val results: MutableMap<String, Map<String, Any>> = HashMap()
        // JVM stats
        results["jvm"] = jvmMemoryMetrics()
        // HTTP requests stats
        results["http.server.requests"] = httpRequestsMetrics()
        // Cache stats
        results["cache"] = cacheMetrics()
        // Service stats
        results["services"] = serviceMetrics()
        // Database stats
        results["databases"] = databaseMetrics()
        // Garbage collector
        results["garbageCollector"] = garbageCollectorMetrics()
        // Process stats
        results["processMetrics"] = processMetrics()
        return results
    }

    internal fun processMetrics(): Map<String, Number> {
        val resultsProcess: MutableMap<String, Number> = HashMap()
        val gauges = Search.`in`(meterRegistry).name { s: String ->
            s.contains("cpu") || s.contains("system") || s.contains("process")
        }.gauges()
        gauges.forEach { gauge: Gauge -> resultsProcess[gauge.id.name] = gauge.value() }

        val timeGauges = Search.`in`(meterRegistry).name { s: String -> s.contains("process") }.timeGauges()
        timeGauges.forEach { gauge: TimeGauge ->
            resultsProcess[gauge.id.name] = gauge.value(TimeUnit.MILLISECONDS)
        }

        return resultsProcess
    }

    internal fun garbageCollectorMetrics(): Map<String, Any> {
        val resultsGarbageCollector: MutableMap<String, Any> = HashMap()
        val timers = Search.`in`(meterRegistry).name { it.contains("jvm.gc.pause") }.timers()
        timers.forEach { timer: Timer ->
            val key = timer.id.name
            val gcPauseResults = HashMap<String, Number>()
            gcPauseResults["count"] = timer.count()
            gcPauseResults["max"] = timer.max(TimeUnit.MILLISECONDS)
            gcPauseResults["totalTime"] = timer.totalTime(TimeUnit.MILLISECONDS)
            gcPauseResults["mean"] = timer.mean(TimeUnit.MILLISECONDS)
            val percentiles = timer.takeSnapshot().percentileValues()
            for (percentile in percentiles) {
                gcPauseResults[percentile.percentile().toString()] = percentile.value(TimeUnit.MILLISECONDS)
            }
            resultsGarbageCollector.putIfAbsent(key, gcPauseResults)
        }

        var gauges = Search.`in`(meterRegistry).name { it.contains("jvm.gc") && !it.contains("jvm.gc.pause") }.gauges()
        gauges.forEach { gauge: Gauge -> resultsGarbageCollector[gauge.id.name] = gauge.value() }
        val counters = Search.`in`(meterRegistry).name {
            it.contains("jvm.gc") && !it.contains("jvm.gc.pause")
        }.counters()
        counters.forEach { counter: Counter -> resultsGarbageCollector[counter.id.name] = counter.count() }
        gauges = Search.`in`(meterRegistry).name { it.contains("jvm.classes.loaded") }.gauges()
        val classesLoaded = gauges.map { obj: Gauge -> obj.value() }.sum()
        resultsGarbageCollector["classesLoaded"] = classesLoaded
        val functionCounters = Search.`in`(meterRegistry).name {
            it.contains("jvm.classes.unloaded")
        }.functionCounters()
        val classesUnloaded = functionCounters.map { obj: FunctionCounter -> obj.count() }.sum()
        resultsGarbageCollector["classesUnloaded"] = classesUnloaded
        return resultsGarbageCollector
    }

    internal fun databaseMetrics(): Map<String, MutableMap<String, Number>> {
        val resultsDatabase: MutableMap<String, MutableMap<String, Number>> = HashMap()
        val timers = Search.`in`(meterRegistry).name { it.contains("hikari") }.timers()
        timers.forEach { timer: Timer ->
            val key = timer.id.name.substring(timer.id.name.lastIndexOf('.') + 1)
            resultsDatabase.getOrPut(key) { HashMap() }.apply {
                this["count"] = timer.count()
                this["max"] = timer.max(TimeUnit.MILLISECONDS)
                this["totalTime"] = timer.totalTime(TimeUnit.MILLISECONDS)
                this["mean"] = timer.mean(TimeUnit.MILLISECONDS)

                val percentiles = timer.takeSnapshot().percentileValues()
                for (percentile in percentiles) {
                    this[percentile.percentile().toString()] = percentile.value(TimeUnit.MILLISECONDS)
                }
            }
        }

        val gauges = Search.`in`(meterRegistry).name { it.contains("hikari") }.gauges()
        gauges.forEach { gauge: Gauge ->
            val key = gauge.id.name.substring(gauge.id.name.lastIndexOf('.') + 1)
            resultsDatabase.getOrPut(key) { HashMap() }["value"] = gauge.value()
        }

        return resultsDatabase
    }

    internal fun serviceMetrics(): Map<String, Map<*, *>> {
        val crudOperation: Collection<String> = listOf("GET", "POST", "PUT", "DELETE")
        val uris: MutableSet<String> = HashSet()
        val timers = meterRegistry.find("http.server.requests").timers()
        timers.forEach { timer: Timer -> timer.id.getTag("uri")?.let { uris.add(it) } }

        val resultsHttpPerUri: MutableMap<String, Map<*, *>> = HashMap()
        uris.forEach { uri: String ->
            val resultsPerUri: MutableMap<String, Map<*, *>> = HashMap()
            crudOperation.forEach { operation: String ->
                val resultsPerUriPerCrudOperation: MutableMap<String, Number> = HashMap()
                val httpTimersStream = meterRegistry.find("http.server.requests")
                    .tags("uri", uri, "method", operation).timers()
                val count = httpTimersStream.map { it.count() }.sum()
                if (count != 0L) {
                    val max = httpTimersStream.map { timer -> timer.max(TimeUnit.MILLISECONDS) }.maxOrNull() ?: 0.0
                    val totalTime = httpTimersStream.map { it.totalTime(TimeUnit.MILLISECONDS) }.sum()
                    resultsPerUriPerCrudOperation["count"] = count
                    resultsPerUriPerCrudOperation["max"] = max
                    resultsPerUriPerCrudOperation["mean"] = totalTime / count
                    resultsPerUri[operation] = resultsPerUriPerCrudOperation
                }
            }
            resultsHttpPerUri[uri] = resultsPerUri
        }

        return resultsHttpPerUri
    }

    internal fun cacheMetrics(): Map<String, MutableMap<String, Number>> {
        val resultsCache: MutableMap<String, MutableMap<String, Number>> = HashMap()
        val counters = Search.`in`(meterRegistry).name {
            it.contains("cache") && !it.contains("hibernate")
        }.functionCounters()
        counters.forEach { counter: FunctionCounter ->
            var key = counter.id.name
            val name = counter.id.getTag("name")
            if (name != null) {
                counter.id.getTag("result")?.let {
                    key += ".$it"
                }
                resultsCache.getOrPut(name) { HashMap() }[key] = counter.count()
            } else {
                logger.warn(MISSING_NAME_TAG_MESSAGE, key)
            }
        }

        val gauges = Search.`in`(meterRegistry).name { it.contains("cache") }.gauges()
        gauges.forEach(
            Consumer { gauge: Gauge ->
                val key = gauge.id.name
                val name = gauge.id.getTag("name")
                if (name != null) {
                    resultsCache.getOrPut(name) { HashMap() }[key] = gauge.value()
                } else {
                    logger.warn(MISSING_NAME_TAG_MESSAGE, key)
                }
            }
        )
        return resultsCache
    }

    internal fun jvmMemoryMetrics(): Map<String, MutableMap<String, Number>> {
        val resultsJvm: MutableMap<String, MutableMap<String, Number>> = HashMap()
        val jvmUsedSearch = Search.`in`(meterRegistry).name { it.contains("jvm.memory.used") }
        var gauges = jvmUsedSearch.gauges()
        gauges.forEach { gauge: Gauge ->
            val key = gauge.id.getTag("id")
            key?.let {
                resultsJvm.getOrPut(it) { HashMap() }["used"] = gauge.value()
            }
        }

        val jvmMaxSearch = Search.`in`(meterRegistry).name { it.contains("jvm.memory.max") }
        gauges = jvmMaxSearch.gauges()
        gauges.forEach { gauge: Gauge ->
            val key = gauge.id.getTag("id")
            key?.let {
                resultsJvm.getOrPut(it) { HashMap() }["max"] = gauge.value()
            }
        }

        gauges = Search.`in`(meterRegistry).name { it.contains("jvm.memory.committed") }.gauges()
        gauges.forEach { gauge: Gauge ->
            val key = gauge.id.getTag("id")
            key?.let {
                resultsJvm.getOrPut(it) { HashMap() }["committed"] = gauge.value()
            }
        }
        return resultsJvm
    }

    internal fun httpRequestsMetrics(): Map<String, Map<*, *>> {
        val statusCode: MutableSet<String> = HashSet()
        var timers = meterRegistry.find("http.server.requests").timers()

        timers.forEach { timer -> timer.id.getTag("status")?.let { statusCode.add(it) } }

        val httpResults: MutableMap<String, Map<*, *>> = HashMap()
        val httpResultsPerCode: MutableMap<String, Map<String, Number>> = HashMap()
        statusCode.forEach { code: String ->
            val resultsPerCode: MutableMap<String, Number> = HashMap()
            val httpTimersStream = meterRegistry.find("http.server.requests").tag("status", code).timers()
            val count = httpTimersStream.map { it.count() }.sum()
            val max = httpTimersStream.map { it.max(TimeUnit.MILLISECONDS) }.maxOrNull() ?: 0.0
            val totalTime = httpTimersStream.map { it.totalTime(TimeUnit.MILLISECONDS) }.sum()
            resultsPerCode["count"] = count
            resultsPerCode["max"] = max
            resultsPerCode["mean"] = if (count != 0L) totalTime / count else 0
            httpResultsPerCode[code] = resultsPerCode
        }
        httpResults["percode"] = httpResultsPerCode
        timers = meterRegistry.find("http.server.requests").timers()
        val countAllRequests = timers.map { it.count() }.sum()
        val httpResultsAll: MutableMap<String, Number> = HashMap()
        httpResultsAll["count"] = countAllRequests
        httpResults["all"] = httpResultsAll
        return httpResults
    }
}
