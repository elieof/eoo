package io.github.elieof.eoo.domain.util

import org.springframework.core.convert.converter.Converter
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class JSR310DateConverters {

    companion object {
        val LOCAL_DATE_TO_DATE =
            Converter<LocalDate?, Date?> { Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant()) }

        val DATE_TO_LOCAL_DATE =
            Converter<Date?, LocalDate?> { LocalDate.ofInstant(it.toInstant(), ZoneId.systemDefault()) }

        val LOCAL_DATETIME_TO_DATE =
            Converter<LocalDateTime?, Date?> { Date.from(it.atZone(ZoneId.systemDefault()).toInstant()) }

        val DATE_TO_LOCAL_DATETIME =
            Converter<Date?, LocalDateTime?> { LocalDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault()) }

        val ZONED_DATETIME_TO_DATE = Converter<ZonedDateTime?, Date?> { Date.from(it.toInstant()) }

        val DATE_TO_ZONED_DATETIME =
            Converter<Date?, ZonedDateTime?> { ZonedDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault()) }

        val DURATION_TO_LONG = Converter<Duration?, Long?> { it.toNanos() }

        val LONG_TO_DURATION = Converter<Long?, Duration?> { Duration.ofNanos(it) }
    }
}
