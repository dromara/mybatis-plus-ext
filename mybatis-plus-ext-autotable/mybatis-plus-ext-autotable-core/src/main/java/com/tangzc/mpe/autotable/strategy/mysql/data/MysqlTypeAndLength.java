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

    /**
     * 声明该字段是否为字符串，将会对字符串做特殊的补偿处理
     */
    private Boolean charString;
    /**
     * 声明该字段是否为布尔类型
     */
    private Boolean bool;
    /**
     * 声明该字段是否为数字类型
     */
    private Boolean number;

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
        return charString != null ? charString : CHAR_STRING_TYPE.contains(this.typeName());
    }

    public boolean isBoolean() {
        return bool != null ? bool : MySqlDefaultTypeEnum.BIT.typeName().equalsIgnoreCase(this.typeName());
    }

    public boolean isNumber() {
        return number != null ? number : (INTEGER_TYPE.contains(this.typeName()) || FLOAT_TYPE.contains(this.typeName()));
    }
}
