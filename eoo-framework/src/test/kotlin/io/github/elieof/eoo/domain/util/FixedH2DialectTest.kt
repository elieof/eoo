package io.github.elieof.eoo.domain.util

import io.github.elieof.eoo.test.LogbackRecorder
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.dialect.Dialect
import org.hibernate.dialect.H2Dialect
import org.junit.jupiter.api.Test
import java.sql.Types
import java.util.*

class FixedH2DialectTest {

    @Test
    fun test() {
        val recorders: MutableList<LogbackRecorder> = LinkedList<LogbackRecorder>()
        recorders.add(LogbackRecorder.forName("org.jboss.logging").reset().capture("ALL"))
        recorders.add(LogbackRecorder.forClass(Dialect::class.java).reset().capture("ALL"))
        recorders.add(LogbackRecorder.forClass(H2Dialect::class.java).reset().capture("ALL"))
        val dialect = FixedH2Dialect()
        assertThat(dialect.getTypeName(Types.FLOAT)).isEqualTo("real")
        recorders.forEach(LogbackRecorder::release)
    }
}
