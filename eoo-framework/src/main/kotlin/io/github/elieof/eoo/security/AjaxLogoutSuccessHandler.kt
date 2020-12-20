package io.github.elieof.eoo.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Spring Security logout handler, specialized for Ajax requests.
 */
class AjaxLogoutSuccessHandler : AbstractAuthenticationTargetUrlRequestHandler(),
    LogoutSuccessHandler {
    /** {@inheritDoc}  */
    @Throws(IOException::class, ServletException::class)
    override fun onLogoutSuccess(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication
    ) {
        response.status = HttpServletResponse.SC_OK
    }
}
