package io.github.elieof.eoo.config.locale

import io.github.elieof.eoo.test.LogbackRecorder
import io.github.elieof.eoo.test.LogbackRecorder.Event
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.i18n.TimeZoneAwareLocaleContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import java.time.ZoneId
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class AngularCookieLocaleResolverTest {
    companion object {
        private val LOCALE_DEFAULT = Locale.UK
        private val LOCALE_CUSTOM = Locale.FRANCE
        private val TIMEZONE_CUSTOM = TimeZone.getTimeZone(ZoneId.of("GMT"))
        private val TIMEZONE_DEFAULT = TimeZone.getTimeZone(ZoneId.of("GMT+01:00"))
    }

    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var resolver: AngularCookieLocaleResolver
    private lateinit var recorder: LogbackRecorder

    private val slot = slot<Cookie>()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        request = spyk(MockHttpServletRequest())
        response = spyk<MockHttpServletResponse>()
        resolver = AngularCookieLocaleResolver()
        resolver.setDefaultLocale(LOCALE_DEFAULT)
        resolver.setDefaultTimeZone(TIMEZONE_DEFAULT)
        recorder = LogbackRecorder.forClass(resolver.javaClass).reset().capture("DEBUG")
    }

    @AfterEach
    fun teardown() {
        recorder.release()
    }

    @Test
    fun testDefaults() {
        // given
        every { request.cookies } returns emptyArray()

        // when
        val context = resolver.resolveLocaleContext(request)

        // then
        verify { request.cookies }

        assertThat(context).isNotNull
        assertThat(context).isInstanceOf(TimeZoneAwareLocaleContext::class.java)
        assertThat((context as TimeZoneAwareLocaleContext).locale).isEqualTo(LOCALE_DEFAULT)
        assertThat(context.timeZone).isEqualTo(TIMEZONE_DEFAULT)
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testPresets() {
        every { request.getAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME) } returns LOCALE_DEFAULT
        every { request.getAttribute(CookieLocaleResolver.TIME_ZONE_REQUEST_ATTRIBUTE_NAME) } returns TIMEZONE_DEFAULT

        val context = resolver.resolveLocaleContext(request)

        verify {
            request.getAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME)
        }

        assertThat(context).isNotNull
        assertThat(context).isInstanceOf(TimeZoneAwareLocaleContext::class.java)
        val locale = (context as TimeZoneAwareLocaleContext).locale
        val zone = context.timeZone
        assertThat(locale).isNotNull
        assertThat(locale).isEqualTo(LOCALE_DEFAULT)
        assertThat(zone).isEqualTo(TIMEZONE_DEFAULT)
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testLocale() {
        val value = LOCALE_CUSTOM.toString()
        val cookie = Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, value)
        every { request.cookies } returns arrayOf(cookie)

        val locale = resolver.resolveLocale(request)

        verify { request.cookies }
        assertThat(locale).isNotNull
        assertThat(locale).isEqualTo(LOCALE_CUSTOM)
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testCookieLocaleWithQuotes() {
        val value = resolver.quote(LOCALE_CUSTOM.toString())
        val cookie = Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, value)
        every { request.cookies } returns arrayOf(cookie)

        val locale = resolver.resolveLocale(request)

        verify { request.cookies }
        assertThat(locale).isNotNull
        assertThat(locale).isEqualTo(LOCALE_CUSTOM)
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testTimeZone() {
        val value = "- " + TIMEZONE_CUSTOM.id
        val cookie = Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, value)
        every { request.cookies } returns arrayOf(cookie)

        val context = resolver.resolveLocaleContext(request)

        verify { request.cookies }
        assertThat(context).isNotNull
        assertThat(context).isInstanceOf(TimeZoneAwareLocaleContext::class.java)
        val locale = (context as TimeZoneAwareLocaleContext).locale
        val zone = context.timeZone
        assertThat(locale).isEqualTo(LOCALE_DEFAULT)
        assertThat(zone).isEqualTo(TIMEZONE_CUSTOM)
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testTimeZoneWithQuotes() {
        val value = resolver.quote("- " + TIMEZONE_CUSTOM.id)
        val cookie = Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, value)
        every { request.cookies } returns arrayOf(cookie)

        val context = resolver.resolveLocaleContext(request)

        verify { request.cookies }

        assertThat(context).isNotNull
        assertThat(context).isInstanceOf(TimeZoneAwareLocaleContext::class.java)
        val locale = (context as TimeZoneAwareLocaleContext).locale
        val zone = context.timeZone
        assertThat(locale).isEqualTo(LOCALE_DEFAULT)
        assertThat(zone).isEqualTo(TIMEZONE_CUSTOM)
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testLocaleAndTimeZone() {
        val value = LOCALE_CUSTOM.toString() + " " + TIMEZONE_CUSTOM.id
        val cookie = Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, value)
        every { request.cookies } returns arrayOf(cookie)

        val context = resolver.resolveLocaleContext(request)

        verify { request.cookies }

        assertThat(context).isNotNull
        assertThat(context).isInstanceOf(TimeZoneAwareLocaleContext::class.java)
        val locale = (context as TimeZoneAwareLocaleContext).locale
        val zone = context.timeZone
        assertThat(locale).isEqualTo(LOCALE_CUSTOM)
        assertThat(zone).isEqualTo(TIMEZONE_CUSTOM)
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testLocaleAndTimeZoneWithQuotes() {
        val value = resolver.quote(LOCALE_CUSTOM.toString() + " " + TIMEZONE_CUSTOM.id)
        val cookie = Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, value)
        every { request.cookies } returns arrayOf(cookie)

        val context = resolver.resolveLocaleContext(request)

        verify { request.cookies }

        assertThat(context).isNotNull
        assertThat(context).isInstanceOf(TimeZoneAwareLocaleContext::class.java)
        val locale = (context as TimeZoneAwareLocaleContext).locale
        val zone = context.timeZone
        assertThat(locale).isEqualTo(LOCALE_CUSTOM)
        assertThat(zone).isEqualTo(TIMEZONE_CUSTOM)
        val events = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testCookieWithQuotes() {
        recorder.release()
        recorder.capture("TRACE")

        val value = LOCALE_CUSTOM.toString()
        resolver.addCookie(response, value)

        verify { response.addCookie(capture(slot)) }

        val cookie = slot.captured
        assertThat(cookie.name).isEqualTo(CookieLocaleResolver.DEFAULT_COOKIE_NAME)
        assertThat(cookie.value).isEqualTo(resolver.quote(value))
        val events = recorder.play()
        assertThat(events).hasSize(1)
        val event: Event = events[0]
        assertThat(event.level).isEqualTo("TRACE")
        assertThat(event.message).isEqualTo(
            "Added cookie [" + CookieLocaleResolver.DEFAULT_COOKIE_NAME + "=" +
                resolver.quote(value) + "]"
        )
        assertThat(event.thrown).isNull()
    }

    @Test
    fun testTraceLogLocale() {
        recorder.release()
        recorder.capture("TRACE")

        val value = LOCALE_CUSTOM.toString()
        val cookie = Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, value)

        every { request.cookies } returns arrayOf(cookie)

        val locale = resolver.resolveLocale((request))

        verify { request.cookies }
        val events = recorder.play()
        assertThat(events).hasSize(1)
        val event: Event = events[0]
        assertThat(event.level).isEqualTo("TRACE")
        assertThat(event.message).isEqualTo("Parsed cookie value [$value] into locale '$locale'")
        assertThat(event.thrown).isNull()
    }

    @Test
    fun testTraceLogLocaleAndTimeZone() {
        recorder.release()
        recorder.capture("TRACE")
        val value = LOCALE_CUSTOM.toString() + " " + TIMEZONE_CUSTOM.id
        val cookie = Cookie(CookieLocaleResolver.DEFAULT_COOKIE_NAME, value)
        every { request.cookies } returns arrayOf(cookie)

        val context = resolver.resolveLocaleContext((request))

        verify { request.cookies }
        assertThat(context).isInstanceOf(TimeZoneAwareLocaleContext::class.java)
        val locale = (context as TimeZoneAwareLocaleContext).locale
        val zone = context.timeZone
        val events = recorder.play()
        assertThat(events).hasSize(1)
        val event: Event = events[0]
        assertThat(event.level).isEqualTo("TRACE")
        assertThat(event.message).isEqualTo(
            (
                "Parsed cookie value [$value] into locale '$locale' and time zone '${zone!!.id}'"
                )
        )
        assertThat(event.thrown).isNull()
    }
}
