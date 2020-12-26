package io.github.elieof.eoo.domain.util

import org.hibernate.dialect.PostgreSQL82Dialect
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor
import java.sql.Types

public class FixedPostgreSQL82Dialect : PostgreSQL82Dialect() {
    init {
        registerColumnType(Types.BLOB, "bytea")
    }

    override fun remapSqlTypeDescriptor(sqlTypeDescriptor: SqlTypeDescriptor): SqlTypeDescriptor? {
        return if (sqlTypeDescriptor.sqlType == Types.BLOB) {
            BinaryTypeDescriptor.INSTANCE
        } else super.remapSqlTypeDescriptor(sqlTypeDescriptor)
    }
}
