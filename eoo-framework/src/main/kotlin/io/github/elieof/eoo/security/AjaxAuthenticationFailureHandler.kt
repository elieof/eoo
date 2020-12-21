package io.github.elieof.eoo.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Returns a 401 error code (Unauthorized) to the client, when Ajax authentication fails.
 */
class AjaxAuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    companion object {
        /** Constant `UNAUTHORIZED_MESSAGE="Authentication failed"`  */
        const val UNAUTHORIZED_MESSAGE = "Authentication failed"
    }

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED_MESSAGE)
    }
}
