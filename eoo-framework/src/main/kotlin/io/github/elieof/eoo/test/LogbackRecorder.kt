package io.github.elieof.eoo.test

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import java.util.WeakHashMap

@ConditionalOnClass(LoggerContext::class)
class LogbackRecorder(
    private val logger: Logger,
    private val events: MutableList<Event> = mutableListOf(),
    var isActive: Boolean = false,
    var isAdditive: Boolean = false,
    var level: Level? = Level.INFO
) {
    private var appender: AppenderBase<ILoggingEvent>

    init {
        appender = object : AppenderBase<ILoggingEvent>() {
            @Synchronized
            override fun append(event: ILoggingEvent) {
                events.add(Event.build(event))
            }
        }
    }

    /**
     * Resets the logger by clearing everything that was recorded so far.
     *
     * @return this
     */
    @Synchronized
    fun reset(): LogbackRecorder {
        events.clear()
        return this
    }

    /**
     * Start capturing whatever is logged for this level of worse.
     *
     * @param level the level at which to start capturing
     * @return this
     */
    fun capture(level: String): LogbackRecorder {
        synchronized(lock) {
            check(!this.isActive) { CAPTURE_EXCEPTION_MESSAGE }
            this.isActive = true
            this.isAdditive = logger.isAdditive
            logger.isAdditive = false
            this.level = logger.level
            logger.level = Level.valueOf(level.toUpperCase())
            logger.addAppender(appender)
            appender.start()
        }
        return this
    }

    /**
     * Stop recording and detach from the logger.
     *
     * @return this
     */
    @Synchronized
    fun release(): LogbackRecorder {
        synchronized(lock) {
            check(this.isActive) { RELEASE_EXCEPTION_MESSAGE }
            appender.stop()
            logger.detachAppender(appender)
            logger.level = level
            logger.isAdditive = this.isAdditive
        }
        this.isActive = false
        return this
    }

    /**
     * Return all recorded events.
     *
     * @return all recorded events so far
     */
    fun play(): List<Event> {
        return events.toList()
    }

    data class Event(
        var marker: Marker?,
        var level: String,
        var message: String,
        var arguments: List<Any?>?,
        var thrown: String?
    ) {
        companion object {
            fun build(event: ILoggingEvent): Event {
                val proxy = event.throwableProxy
                val thrown = if (proxy == null) null else proxy.className + ": " + proxy.message
                return Event(
                    event.marker,
                    event.level.toString(),
                    event.message,
                    listOf(event.argumentArray),
                    thrown
                )
            }
        }
    }

    companion object {
        /** Constant `DEFAULT_MUTE=true`  */
        const val DEFAULT_MUTE = true

        /** Constant `DEFAULT_LEVEL="ALL"`  */
        const val DEFAULT_LEVEL = "ALL"

        /** Constant `LOGBACK_EXCEPTION_MESSAGE="Expected logback"`  */
        const val LOGBACK_EXCEPTION_MESSAGE = "Expected logback"

        /** Constant `CAPTURE_EXCEPTION_MESSAGE="Already capturing"`  */
        const val CAPTURE_EXCEPTION_MESSAGE = "Already capturing"

        /** Constant `RELEASE_EXCEPTION_MESSAGE="Not currently capturing"`  */
        const val RELEASE_EXCEPTION_MESSAGE = "Not currently capturing"

        private val context = LoggerFactory.getILoggerFactory() as LoggerContext

        private val lock = context.configurationLock

        private val instances: MutableMap<Logger?, LogbackRecorder> = WeakHashMap(32, 0.75f)

        /**
         * Create a recorder for a logback logger identified by the class name. Instances of a recorder are cached per logger.
         * Make sure to reset it before starting capture.
         *
         * @param clazz class whose logger as its name
         * @return the recorder for this class
         */
        fun forClass(clazz: Class<*>): LogbackRecorder {
            return forLogger(context.getLogger(clazz))
        }

        /**
         * Create a recorder for a logback logger identified by its name. Instances of a recorder are cached per logger.
         * Make sure to reset it before starting capture.
         *
         * @param name the name of the logger
         * @return the recorder for this class
         */
        fun forName(name: String): LogbackRecorder {
            return forLogger(context.getLogger(name))
        }

        /**
         * Create a recorder for a logback logger. Instances of a recorder are cached per logger.
         * Make sure to reset it before starting capture.
         *
         * @param logger the logger to record
         * @return the recorder for this logger
         */
        private fun forLogger(logger: org.slf4j.Logger): LogbackRecorder {
            synchronized(instances) {
                require(logger is Logger) { LOGBACK_EXCEPTION_MESSAGE }
                var recorder = instances[logger]
                if (recorder == null) {
                    recorder = LogbackRecorder(logger)
                    instances[recorder.logger] = recorder
                }
                return recorder
            }
        }
    }
}
