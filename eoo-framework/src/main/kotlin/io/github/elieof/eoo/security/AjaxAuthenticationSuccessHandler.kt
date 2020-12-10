package io.github.elieof.eoo.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
class AjaxAuthenticationSuccessHandler: SimpleUrlAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        response!!.status = HttpServletResponse.SC_OK
    }
}
