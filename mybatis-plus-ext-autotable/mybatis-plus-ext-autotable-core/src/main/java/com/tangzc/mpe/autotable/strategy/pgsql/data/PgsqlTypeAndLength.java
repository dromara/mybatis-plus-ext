package com.tangzc.mpe.autotable.strategy.pgsql.data;

import com.tangzc.mpe.autotable.strategy.pgsql.data.enums.PgsqlDefaultTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author don
 */
@Data
@NoArgsConstructor
public class PgsqlTypeAndLength {
    private Integer length;
    private Integer decimalLength;
    private String type;

    public PgsqlTypeAndLength(Integer length, Integer decimalLength, String type) {
        if (length != null && length >= 0) {
            this.length = length;
        }
        if (decimalLength != null && decimalLength >= 0) {
            this.decimalLength = decimalLength;
        }
        this.type = type;
    }

    public String getFullType() {
        String typeAndLength = this.type;
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

    public String typeName() {
        return this.type.toLowerCase();
    }

    public boolean isCharString() {
        return PgsqlDefaultTypeEnum.CHAR.typeName().equalsIgnoreCase(this.typeName()) ||
                PgsqlDefaultTypeEnum.VARCHAR.typeName().equalsIgnoreCase(this.typeName()) ||
                PgsqlDefaultTypeEnum.TEXT.typeName().equalsIgnoreCase(this.typeName());
    }

    public boolean isBoolean() {
        return PgsqlDefaultTypeEnum.BOOL.typeName().equalsIgnoreCase(this.typeName());
    }

    public boolean isTime() {
        return PgsqlDefaultTypeEnum.DATE.typeName().equalsIgnoreCase(this.typeName())
                || PgsqlDefaultTypeEnum.TIMESTAMP.typeName().equalsIgnoreCase(this.typeName())
                || PgsqlDefaultTypeEnum.TIME.typeName().equalsIgnoreCase(this.typeName());
    }
}
