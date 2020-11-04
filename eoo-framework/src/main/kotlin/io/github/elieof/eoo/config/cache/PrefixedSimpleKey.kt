package io.github.elieof.eoo.config.cache

import java.io.Serializable

public data class PrefixedSimpleKey(
    var prefix: String,
    var methodName: String,
    var params: List<Any>,
) : Serializable
