package com.tangzc.mpe.autotable.constants;

import lombok.Getter;

/**
 * @author don
 */
@Getter
public enum DatabaseDialect {

    /**
     * MySQL数据库
     */
    MySQL,

    /**
     * SQLite数据库
     */
    SQLite,

    /**
     * PostgreSQL数据库
     */
    PostgreSQL;

    public static DatabaseDialect parseFromDriverName(String driverName) {

        for (DatabaseDialect dialect : DatabaseDialect.values()) {
            if (driverName.toLowerCase().startsWith(dialect.name().toLowerCase())) {
                return dialect;
            }
        }

        return null;
    }
}
