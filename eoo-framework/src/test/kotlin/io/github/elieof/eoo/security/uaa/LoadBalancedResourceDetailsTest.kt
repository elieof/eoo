package io.github.elieof.eoo.security.uaa


import io.github.elieof.eoo.security.uaa.LoadBalancedResourceDetails.Companion.EXCEPTION_MESSAGE
import io.github.elieof.eoo.test.LogbackRecorder
import io.github.elieof.eoo.test.LogbackRecorder.Event
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import java.net.URI


internal class LoadBalancedResourceDetailsTest {
    companion object {
        private const val ACCESS_TOKEN_URI = "https://access.token.uri/"
        private const val TOKEN_SERVICE_ID = "tokkie"
    }

    private lateinit var client: LoadBalancerClient
    private lateinit var recorder: LogbackRecorder

    @BeforeEach
    fun setup() {
        client = spy(LoadBalancerClient::class.java)
        doReturn(null).`when`(client).choose(TOKEN_SERVICE_ID)
        doAnswer { invocation -> invocation.getArgument<URI>(1) }.`when`(client)
            .reconstructURI(any(), any())
        recorder = LogbackRecorder.forClass(LoadBalancedResourceDetails::class.java).reset().capture("ALL")
    }

    @AfterEach
    fun teardown() {
        recorder.release()
    }

    @Test
    fun testWithClient() {
        val details = LoadBalancedResourceDetails(client)
        details.accessTokenUri = ACCESS_TOKEN_URI
        assertThat(details.accessTokenUri).isEqualTo(ACCESS_TOKEN_URI)
        verify(client, never()).reconstructURI(any(), any())
        val events: List<Event> = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testWithClientAndEmptyTokenService() {
        val details = LoadBalancedResourceDetails(client)
        details.accessTokenUri = ACCESS_TOKEN_URI
        assertThat(details.accessTokenUri).isEqualTo(ACCESS_TOKEN_URI)
        assertThat(details.tokenServiceId).isEmpty()
        verify(client, never()).reconstructURI(any(), any())
        val events: List<Event> = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testWithClientAndTokenService() {
        val details = LoadBalancedResourceDetails(client, TOKEN_SERVICE_ID)
        details.accessTokenUri = ACCESS_TOKEN_URI
        assertThat(details.accessTokenUri).isEqualTo(ACCESS_TOKEN_URI)
        assertThat(details.tokenServiceId).isEqualTo(TOKEN_SERVICE_ID)
        val captor = ArgumentCaptor.forClass(URI::class.java)
        verify(client).reconstructURI(any(), captor.capture())
        assertThat(captor.value.toString()).isEqualTo(ACCESS_TOKEN_URI)
        val events: List<Event> = recorder.play()
        assertThat(events).isEmpty()
    }

    @Test
    fun testInvalidAccessTokenURI() {
        val invalidUri = "%"
        catchThrowable {
            URI(invalidUri)
        }
        val details = LoadBalancedResourceDetails(client, TOKEN_SERVICE_ID)
        details.accessTokenUri = invalidUri
        assertThat(details.accessTokenUri).isEqualTo(invalidUri)
        val events: List<Event> = recorder.play()
        assertThat(events).hasSize(1)
        val event: Event = events[0]
        assertThat(event.level).isEqualTo("ERROR")
        assertThat(event.message).isEqualTo(EXCEPTION_MESSAGE)
        assertThat(event.thrown).isNull()
    }
}

