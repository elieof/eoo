package io.github.elieof.eoo.domain.util

import org.hibernate.dialect.H2Dialect
import java.sql.Types

public class FixedH2Dialect : H2Dialect() {

    init {
        registerColumnType(Types.FLOAT, "real")
    }
}
