package io.github.elieof.eoo.security

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
        val cache: PersistentTokenCache<String> = PersistentTokenCache(1L)
        cache.put("key", "val")
        GlobalScope.launch { // launch a new coroutine in background and continue
            delay(10L)
            assertThat(cache["key"]).isNull()
        }
    }

    @Test
    fun testPurge() {
        val cache: PersistentTokenCache<String> = PersistentTokenCache(1L)
        cache.put("key", "val")
        GlobalScope.launch { // launch a new coroutine in background and continue
            delay(10L)
        }
        assertThat(cache.size()).isEqualTo(1)
        cache.purge()
        assertThat(cache.size()).isEqualTo(0)
    }
}
