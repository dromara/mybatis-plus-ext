package com.tangzc.mpe.autotable.strategy.sqlite.data;

import com.tangzc.mpe.autotable.strategy.sqlite.data.enums.SqliteDefaultTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author don
 */
@Data
@NoArgsConstructor
public class SqliteTypeAndLength {
    private Integer length;
    private Integer decimalLength;
    private String type;

    public SqliteTypeAndLength(Integer length, Integer decimalLength, String type) {
        this.length = length;
        this.decimalLength = decimalLength;

        // 纠正类型的写法为正规方式
        String upperName = type.toUpperCase();
        if (upperName.contains("INT")) {
            type = "INTEGER";
        }
        if (upperName.contains("CHAR") || upperName.contains("CLOB") || upperName.contains("TEXT")) {
            type = "TEXT";
        }
        if (upperName.contains("BLOB")) {
            type = "BLOB";
        }
        if (upperName.contains("REAL") || upperName.contains("FLOA") || upperName.contains("DOUB")) {
            type = "REAL";
        }
        this.type = type;
    }

    public String getFullType() {
        // 例：double(4,2) unsigned zerofill
        String typeAndLength = this.type;
        // 类型具备长度属性 且 自定义长度不为空
        if (this.length != null && this.length > 0) {
            typeAndLength += "(" + this.length;
            if (this.decimalLength != null && this.decimalLength > 0) {
                typeAndLength += "," + this.decimalLength;
            }
            typeAndLength += ")";
        }

        return typeAndLength;
    }

    public boolean isText() {
        return SqliteDefaultTypeEnum.TEXT.typeName().equalsIgnoreCase(this.type);
    }

    public boolean isInteger() {
        return SqliteDefaultTypeEnum.INTEGER.typeName().equalsIgnoreCase(this.type);
    }
}
