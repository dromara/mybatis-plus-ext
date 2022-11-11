package com.tangzc.mpe.datasource.description;

import com.tangzc.mpe.base.util.TableColumnUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
public class WaitUpdateFieldDescription {
    /**
     * 需要冗余的类的具体字段
     */
    private Field entityField;

    /**
     * 数据来源的Entity对应的属性
     */
    private Field sourceField;

    public String getEntityFieldName() {
        return entityField.getName();
    }

    public String getSourceFieldName() {
        return sourceField.getName();
    }

    public String getEntityColumnName() {
        return TableColumnUtil.getRealColumnName(entityField);
    }

    public String getSourceColumnName() {
        return TableColumnUtil.getRealColumnName(sourceField);
    }
}
