package com.tangzc.mpe.autotable.strategy.mysql.data;

import com.google.common.collect.Sets;
import com.tangzc.mpe.autotable.strategy.mysql.data.enums.MySqlDefaultTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author don
 */
@Data
@NoArgsConstructor
public class MysqlTypeAndLength {
    private Integer length;
    private Integer decimalLength;
    private String type;

    public MysqlTypeAndLength(Integer length, Integer decimalLength, String type) {
        this.length = length;
        this.decimalLength = decimalLength;
        this.type = type;
    }

    private static final Set<String> CHAR_STRING_TYPE = Sets.newHashSet(
            MySqlDefaultTypeEnum.CHAR.typeName(),
            MySqlDefaultTypeEnum.VARCHAR.typeName(),
            MySqlDefaultTypeEnum.TEXT.typeName(),
            MySqlDefaultTypeEnum.TINYTEXT.typeName(),
            MySqlDefaultTypeEnum.MEDIUMTEXT.typeName(),
            MySqlDefaultTypeEnum.LONGTEXT.typeName()
    );

    private static final Set<String> INTEGER_TYPE = Sets.newHashSet(
            MySqlDefaultTypeEnum.INT.typeName(),
            MySqlDefaultTypeEnum.TINYINT.typeName(),
            MySqlDefaultTypeEnum.SMALLINT.typeName(),
            MySqlDefaultTypeEnum.MEDIUMINT.typeName(),
            MySqlDefaultTypeEnum.BIGINT.typeName()
    );

    private static final Set<String> FLOAT_TYPE = Sets.newHashSet(
            MySqlDefaultTypeEnum.FLOAT.typeName(),
            MySqlDefaultTypeEnum.DOUBLE.typeName(),
            MySqlDefaultTypeEnum.DECIMAL.typeName()
    );

    public String typeName() {
        return this.type.toLowerCase();
    }

    public String getFullType() {
        // 例：double(4,2) unsigned zerofill
        String typeAndLength = typeName();
        // 类型具备长度属性 且 自定义长度不为空

        if (this.length != null) {
            typeAndLength += "(" + this.length;
            if (this.decimalLength != null) {
                typeAndLength += "," + this.decimalLength;
            }
            typeAndLength += ")";
        }

        return typeAndLength;
    }


    public boolean isCharString() {
        return CHAR_STRING_TYPE.contains(this.typeName());
    }

    public boolean isBoolean() {
        return MySqlDefaultTypeEnum.BIT.typeName().equalsIgnoreCase(this.typeName());
    }

    public boolean isNumber() {
        return (INTEGER_TYPE.contains(this.typeName()) || FLOAT_TYPE.contains(this.typeName()));
    }
}
