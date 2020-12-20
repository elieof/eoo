/*
 * Copyright 2016-2020 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.elieof.eoo.security


/**
 * Simple time-limited cache for login tokens, necessary to avoid concurrent
 * requests invalidating one another. It uses a [kotlin.collections.LinkedHashMap]
 * to keep the tokens in order of expiration. During access any entries which
 * have expired are automatically purged.
 */
class PersistentTokenCache<T>(expireMillis: Long) {
    private val expireMillis: Long
    private val map: MutableMap<String, Value>
    private var latestWriteTime: Long

    companion object {
        const val CAPACITY = 64
        const val LOAD_FACTOR = 0.75f
    }

    init {
        require(expireMillis > 0L)
        this.expireMillis = expireMillis
        map = LinkedHashMap(CAPACITY, LOAD_FACTOR)
        latestWriteTime = System.currentTimeMillis()
    }

    /**
     * Get a token from the cache.
     *
     * @param key The key to look for.
     * @return The token, if present and not yet expired, or null otherwise.
     */
    operator fun get(key: String): T? {
        purge()
        val value: Value? = map[key]
        val time = System.currentTimeMillis()
        return if (value != null && time < value.expire) value.token else null
    }

    /**
     * Put a token in the cache.
     * If a token already exists for the given key, it is replaced.
     *
     * @param key   The key to insert for.
     * @param token The token to insert.
     */
    fun put(key: String, token: T) {
        purge()
        if (map.containsKey(key)) {
            map.remove(key)
        }
        val time = System.currentTimeMillis()
        map[key] = Value(token, time + expireMillis)
        latestWriteTime = time
    }

    /**
     * Get the number of tokens in the cache. Note, this may include expired
     * tokens, unless [.purge] is invoked first.
     *
     * @return The size of the cache.
     */
    fun size(): Int {
        return map.size
    }

    /**
     * Remove expired entries from the map. This will be called automatically
     * before read/write access, but could be manually invoked if desired.
     */
    fun purge() {
        val time = System.currentTimeMillis()
        if (time - latestWriteTime > expireMillis) {
            // Everything in the map is expired, clear all at once
            map.clear()
        } else {
            // Iterate and remove until the first non-expired token
            val values: MutableIterator<Value> = map.values.iterator()
            while (values.hasNext()) {
                if (time >= values.next().expire) {
                    values.remove()
                } else {
                    break
                }
            }
        }
    }

    private inner class Value(val token: T, var expire: Long)
}
