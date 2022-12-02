package com.tangzc.mpe.autotable.strategy.mysql.data;

import com.google.common.collect.Sets;
import com.tangzc.mpe.autotable.strategy.mysql.data.enums.MySqlColumnTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author don
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAndLength {
    private Integer length;
    private Integer decimalLength;
    private MySqlColumnTypeEnum type;

    private static final Set<MySqlColumnTypeEnum> CHAR_STRING_TYPE = Sets.newHashSet(
            MySqlColumnTypeEnum.CHAR,
            MySqlColumnTypeEnum.VARCHAR,
            MySqlColumnTypeEnum.TEXT,
            MySqlColumnTypeEnum.TINYTEXT,
            MySqlColumnTypeEnum.MEDIUMTEXT,
            MySqlColumnTypeEnum.LONGTEXT
    );

    private static final Set<MySqlColumnTypeEnum> INTEGER_TYPE = Sets.newHashSet(
            MySqlColumnTypeEnum.INT,
            MySqlColumnTypeEnum.TINYINT,
            MySqlColumnTypeEnum.SMALLINT,
            MySqlColumnTypeEnum.MEDIUMINT,
            MySqlColumnTypeEnum.BIGINT
    );

    private static final Set<MySqlColumnTypeEnum> FLOAT_TYPE = Sets.newHashSet(
            MySqlColumnTypeEnum.FLOAT,
            MySqlColumnTypeEnum.DOUBLE,
            MySqlColumnTypeEnum.DECIMAL
    );

    public String typeName() {
        return this.type.name().toLowerCase();
    }

    public String getFullType() {
        // 例：double(4,2) unsigned zerofill
        String typeAndLength = typeName();
        // 类型具备长度属性 且 自定义长度不为空
        if (type.getLengthDefault() != null) {
            typeAndLength += "(" + (length != null ? length : type.getLengthDefault());
            if (type.getDecimalLengthDefault() != null) {
                typeAndLength += "," + (decimalLength != null ? decimalLength : type.getDecimalLengthDefault());
            }
            typeAndLength += ")";
        }

        return typeAndLength;
    }


    public boolean isCharString() {
        return CHAR_STRING_TYPE.contains(this.type);
    }

    public boolean isBoolean() {
        return this.type == MySqlColumnTypeEnum.BIT;
    }

    public boolean isNumber() {
        return INTEGER_TYPE.contains(this.type) || FLOAT_TYPE.contains(this.type);
    }

    public boolean isIntegerOrLong() {
        return INTEGER_TYPE.contains(this.type);
    }
}
