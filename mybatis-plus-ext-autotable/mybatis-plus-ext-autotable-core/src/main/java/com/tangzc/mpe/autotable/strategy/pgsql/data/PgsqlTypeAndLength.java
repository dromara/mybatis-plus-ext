package com.tangzc.mpe.autotable.strategy.pgsql.data;

import com.tangzc.mpe.autotable.strategy.pgsql.data.enums.PgsqlDefaultTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author don
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PgsqlTypeAndLength {
    private Integer length;
    private Integer decimalLength;
    private String type;


    public String getFullType() {
        // 例：double(4,2) unsigned zerofill
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
}
