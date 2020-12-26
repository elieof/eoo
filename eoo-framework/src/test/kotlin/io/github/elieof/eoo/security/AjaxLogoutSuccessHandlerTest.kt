package io.github.elieof.eoo.security

import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.Authentication
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AjaxLogoutSuccessHandlerTest {

    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var authentication: Authentication
    private lateinit var handler: AjaxLogoutSuccessHandler

    @BeforeEach
    fun setUp() {
        request = mockk()
        response = spyk()
        authentication = mockk()
        handler = AjaxLogoutSuccessHandler()
    }

    @Test
    fun onLogoutSuccess() {
        val caughtException = Assertions.catchThrowable {
            handler.onLogoutSuccess(request, response, authentication)
            verify { response.status = HttpServletResponse.SC_OK }
        }
        Assertions.assertThat(caughtException).isNull()
    }
}
