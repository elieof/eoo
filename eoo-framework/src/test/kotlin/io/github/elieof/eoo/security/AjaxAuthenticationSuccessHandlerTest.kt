package io.github.elieof.eoo.security

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions
import javax.servlet.http.HttpServletResponse

internal class AjaxAuthenticationSuccessHandlerTest {

    private lateinit var response: HttpServletResponse
    private lateinit var handler: AjaxAuthenticationSuccessHandler

    @BeforeEach
    fun setUp() {
        response = spyk()
        handler = AjaxAuthenticationSuccessHandler()
    }

    @Test
    fun onAuthenticationSuccess() {
        val caughtException = Assertions.catchThrowable {
            handler.onAuthenticationSuccess(null, response, null)
            verify {response.status = HttpServletResponse.SC_OK }
        }
        Assertions.assertThat(caughtException).isNull()
    }
}
