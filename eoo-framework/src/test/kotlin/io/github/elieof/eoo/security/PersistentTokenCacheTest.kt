package io.github.elieof.eoo.security

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock

class PersistentTokenCacheTest {

    @Test
    fun testConstructorThrows() {
        val caught = Assertions.catchThrowable {
            PersistentTokenCache<String>(-1L)
        }
        assertThat(caught).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testAbsent() {
        val cache: PersistentTokenCache<String> = PersistentTokenCache(100L)
        assertThat(cache["key"]).isNull()
    }

    @Test
    fun testAccess() {
        val cache: PersistentTokenCache<String> = PersistentTokenCache(100L)
        cache.put("key", "val")
        assertThat(cache.size()).isEqualTo(1)
        assertThat(cache["key"]).isEqualTo("val")
    }

    @Test
    fun testReplace() {
        val cache: PersistentTokenCache<String> = PersistentTokenCache(100L)
        cache.put("key", "val")
        cache.put("key", "foo")
        assertThat(cache["key"]).isEqualTo("foo")
    }

    @Test
    fun testExpires() {
        val cache: PersistentTokenCache<String> = PersistentTokenCache(9L)
        cache.put("key", "val")
        runBlocking{
            delay(10L)
            assertThat(cache["key"]).isNull()
        }
    }

    @Test
    fun testExpiresAfterPurge() = runBlocking {
        val cache: PersistentTokenCache<String> = spyk(PersistentTokenCache(9L))
        every { cache.put(any(), any()) } answers { callOriginal() }
        justRun { cache.purge() }
        cache.put("key", "val")
        delay(10L)
        println(cache)
        assertThat(cache["key"]).isNull()
    }

    @Test
    fun testPurgeClear() {
        val cache: PersistentTokenCache<String> = PersistentTokenCache(1L)
        cache.put("key", "val")
        runBlocking {
            delay(10L)
            assertThat(cache.size()).isEqualTo(1)
            cache.purge()
            assertThat(cache.size()).isEqualTo(0)
        }
    }

    @Test
    fun testPurgeExpired() {
        val cache: PersistentTokenCache<String> = PersistentTokenCache(4000L)
        cache.put("key", "val")
        runBlocking {
            delay(2000L)
            cache.put("key2", "val2")
            println(cache)
            assertThat(cache.size()).isEqualTo(2)
            delay(2000L)
            cache.purge()
            assertThat(cache.size()).isEqualTo(1)
        }
    }

    @Test
    fun testPurgeExpiredWithMockK()  {
        val clock: Clock = mockk()
        every { clock.millis() } returnsMany listOf(0L, 0L, 0L, 2000L, 2001L, 4001L)
        val cache: PersistentTokenCache<String> = PersistentTokenCache(4000L, clock)
        cache.put("key", "val")

        cache.put("key2", "val2")
        assertThat(cache.size()).isEqualTo(2)
        cache.purge()
        assertThat(cache.size()).isEqualTo(1)
    }
}
