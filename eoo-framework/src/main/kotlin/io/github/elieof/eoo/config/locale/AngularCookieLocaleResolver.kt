package io.github.elieof.eoo.config.locale

import mu.KotlinLogging
import org.springframework.context.i18n.LocaleContext
import org.springframework.context.i18n.TimeZoneAwareLocaleContext
import org.springframework.util.StringUtils
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.util.WebUtils
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

/**
 * Angular cookie saved the locale with a double quote (%22en%22). So the default
 * CookieLocaleResolver#StringUtils.parseLocaleString(localePart)
 * is not able to parse the locale.
 * This class will check if a double quote has been added, if so it will remove it.
 */
public class AngularCookieLocaleResolver : CookieLocaleResolver() {

    public companion object {
        /** Constant `QUOTE="%22"`  */
        public const val QUOTE: String = "%22"
    }

    override fun resolveLocale(request: HttpServletRequest): Locale {
        parseAngularCookieIfNecessary(request)
        return (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) as Locale)
    }

    override fun resolveLocaleContext(request: HttpServletRequest): LocaleContext {
        parseAngularCookieIfNecessary(request)
        return object : TimeZoneAwareLocaleContext {
            override fun getLocale(): Locale = request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) as Locale

            override fun getTimeZone(): TimeZone = request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME) as TimeZone
        }
    }

    override fun addCookie(response: HttpServletResponse, cookieValue: String): Unit =
        super.addCookie(response, quote(cookieValue))

    private fun parseAngularCookieIfNecessary(request: HttpServletRequest) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) != null) return
        // Retrieve and parse cookie value.
        val cookie = cookieName?.let { WebUtils.getCookie(request, it) }
        var locale: Locale? = null
        var timeZone: TimeZone? = null
        if (cookie != null) {
            // Remove the double quote
            val value = cookie.value.replace(QUOTE, "")
            var localePart = value
            val spaceIndex = localePart.indexOf(' ')
            if (spaceIndex != -1) {
                localePart = value.substring(0, spaceIndex)
                timeZone = StringUtils.parseTimeZoneString(value.substring(spaceIndex + 1))
            }
            locale = if ("-" != localePart) StringUtils.parseLocaleString(localePart.replace('-', '_')) else null
            log.trace(
                "Parsed cookie value [${cookie.value}] into locale '$locale'" +
                        if (timeZone != null) " and time zone '${timeZone.id}'" else ""
            )
        }
        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale ?: determineDefaultLocale(request))
        request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, timeZone ?: determineDefaultTimeZone(request))
    }

    public fun quote(string: String): String = QUOTE + string + QUOTE
}
