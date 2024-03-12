package com.tangzc.mpe.bind.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class ColumnDescription {

    private final String columnName;

    private final String fieldName;

    public ColumnDescription(String columnName, String fieldName) {
        this.columnName = columnName;
        this.fieldName = fieldName;
    }
}
