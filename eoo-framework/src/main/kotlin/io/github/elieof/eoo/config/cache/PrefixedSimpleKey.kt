package io.github.elieof.eoo.config.cache

import java.io.Serializable

public data class PrefixedSimpleKey(
    val prefix: String,
    val methodName: String,
    val params: List<Any>,
) : Serializable
