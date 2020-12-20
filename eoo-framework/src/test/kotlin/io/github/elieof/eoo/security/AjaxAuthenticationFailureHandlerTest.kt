package io.github.elieof.eoo.security

import io.github.elieof.eoo.security.AjaxAuthenticationFailureHandler.Companion.UNAUTHORIZED_MESSAGE
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.AuthenticationException
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED

internal class AjaxAuthenticationFailureHandlerTest {

    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var authenticationException: AuthenticationException
    private lateinit var handler: AjaxAuthenticationFailureHandler

    @BeforeEach
    fun setUp() {
        request = mockk()
        response = spyk()
        authenticationException = mockk()
        handler = AjaxAuthenticationFailureHandler()
    }

    @Test
    fun onAuthenticationFailure() {
        val caughtException = Assertions.catchThrowable {
            handler.onAuthenticationFailure(request, response, authenticationException)
            verify { response.sendError(SC_UNAUTHORIZED, UNAUTHORIZED_MESSAGE) }
        }
        Assertions.assertThat(caughtException).isNull()
    }

    @Test
    fun testOnAuthenticationFailureWithException() {
        val exception = IOException("Eek")
        every { response.sendError(any(), any()) } throws exception
        val caught = Assertions.catchThrowable {
            handler.onAuthenticationFailure(request, response, authenticationException)
        }
        Assertions.assertThat(caught).isEqualTo(exception)
    }
}
