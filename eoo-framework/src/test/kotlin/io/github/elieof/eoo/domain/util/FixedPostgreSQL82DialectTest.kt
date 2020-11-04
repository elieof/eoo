package io.github.elieof.eoo.domain.util

import io.github.elieof.eoo.test.LogbackRecorder
import org.assertj.core.api.Assertions
import org.hibernate.dialect.Dialect
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor
import org.hibernate.type.descriptor.sql.BooleanTypeDescriptor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Types
import java.util.*

class FixedPostgreSQL82DialectTest {
    private val recorders: MutableList<LogbackRecorder> = LinkedList<LogbackRecorder>()

    private lateinit var dialect: FixedPostgreSQL82Dialect

    @BeforeEach
    fun setup() {
        recorders.add(LogbackRecorder.forName("org.jboss.logging").reset().capture("ALL"))
        recorders.add(LogbackRecorder.forClass(Dialect::class.java).reset().capture("ALL"))
        dialect = FixedPostgreSQL82Dialect()
    }

    @AfterEach
    fun teardown() {
        recorders.forEach(LogbackRecorder::release)
        recorders.clear()
    }

    @Test
    fun testBlobTypeRegister() {
        Assertions.assertThat(dialect.getTypeName(Types.BLOB)).isEqualTo("bytea")
    }

    @Test
    fun testBlobTypeRemap() {
        val descriptor = dialect.remapSqlTypeDescriptor(BlobTypeDescriptor.DEFAULT)
        Assertions.assertThat(descriptor).isEqualTo(BinaryTypeDescriptor.INSTANCE)
    }

    @Test
    fun testOtherTypeRemap() {
        val descriptor = dialect.remapSqlTypeDescriptor(BooleanTypeDescriptor.INSTANCE)
        Assertions.assertThat(descriptor).isEqualTo(BooleanTypeDescriptor.INSTANCE)
    }
}
