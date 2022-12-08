package com.tangzc.mpe.autotable.strategy.sqlite.data.enums;

/**
 * @author don
 */

public enum SqliteTypeEnum {

    INTEGER,
    REAL,
    TEXT,
    BLOB;

    public static SqliteTypeEnum parse(String typeName) {
        String upperName = typeName.toUpperCase();
        if (upperName.contains("INT")) {
            return INTEGER;
        }
        if (upperName.contains("CHAR") || upperName.contains("CLOB") || upperName.contains("TEXT")) {
            return TEXT;
        }
        if (upperName.contains("BLOB")) {
            return BLOB;
        }
        if (upperName.contains("REAL") || upperName.contains("FLOA") || upperName.contains("DOUB")) {
            return REAL;
        }
        return TEXT;
    }
}
