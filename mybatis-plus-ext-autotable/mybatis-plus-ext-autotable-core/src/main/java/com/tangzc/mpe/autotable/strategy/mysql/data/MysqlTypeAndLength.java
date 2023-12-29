package com.tangzc.mpe.autotable.strategy.mysql.data;

import com.tangzc.mpe.autotable.strategy.mysql.data.enums.MySqlDefaultTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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
        if (length != null && length >= 0) {
            this.length = length;
        }
        if (decimalLength != null && decimalLength >= 0) {
            this.decimalLength = decimalLength;
        }
        this.type = type;
    }

    private static final Set<String> CHAR_STRING_TYPE = new HashSet<String>() {{
        add(MySqlDefaultTypeEnum.CHAR.typeName());
        add(MySqlDefaultTypeEnum.VARCHAR.typeName());
        add(MySqlDefaultTypeEnum.TEXT.typeName());
        add(MySqlDefaultTypeEnum.TINYTEXT.typeName());
        add(MySqlDefaultTypeEnum.MEDIUMTEXT.typeName());
        add(MySqlDefaultTypeEnum.LONGTEXT.typeName());
    }};

    private static final Set<String> DATE_TIME_TYPE = new HashSet<String>() {{
        add(MySqlDefaultTypeEnum.DATE.typeName());
        add(MySqlDefaultTypeEnum.DATETIME.typeName());
        add(MySqlDefaultTypeEnum.YEAR.typeName());
        add(MySqlDefaultTypeEnum.TIME.typeName());
    }};

    private static final Set<String> INTEGER_TYPE = new HashSet<String>() {{
        add(MySqlDefaultTypeEnum.INT.typeName());
        add(MySqlDefaultTypeEnum.TINYINT.typeName());
        add(MySqlDefaultTypeEnum.SMALLINT.typeName());
        add(MySqlDefaultTypeEnum.MEDIUMINT.typeName());
        add(MySqlDefaultTypeEnum.BIGINT.typeName());
    }};

    private static final Set<String> FLOAT_TYPE = new HashSet<String>() {{
        add(MySqlDefaultTypeEnum.FLOAT.typeName());
        add(MySqlDefaultTypeEnum.DOUBLE.typeName());
        add(MySqlDefaultTypeEnum.DECIMAL.typeName());
    }};

    public String typeName() {
        return this.type.toLowerCase();
    }

    public String getFullType() {
        // 例：double(4,2) unsigned zerofill
        String typeAndLength = this.typeName();
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

    public boolean isDateTime() {
        return DATE_TIME_TYPE.contains(this.typeName());
    }

    public boolean needStringCompatibility() {
        return isCharString() || isDateTime();
    }

    public boolean isBoolean() {
        return MySqlDefaultTypeEnum.BIT.typeName().equalsIgnoreCase(this.typeName());
    }

    public boolean isNumber() {
        return (INTEGER_TYPE.contains(this.typeName()) || FLOAT_TYPE.contains(this.typeName()));
    }

    public boolean isFloatNumber() {
        return FLOAT_TYPE.contains(this.typeName());
    }

    public boolean isNoLengthNumber() {
        return INTEGER_TYPE.contains(this.typeName());
    }
}
