package com.tangzc.mpe.datasource.description;

import com.tangzc.mpe.magic.TableColumnUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
public class DataSourceConditionDescription {

    /**
     * 关联数据来源Entity所需的自身字段
     */
    private Field selfField;

    /**
     * 数据来源的Entity的字段，默认为id
     */
    private Field sourceField;

    public String getSelfFieldName() {
        return selfField.getName();
    }

    public String getSourceFieldName() {
        return sourceField.getName();
    }

    public String getSelfColumnName() {
        return TableColumnUtil.getRealColumnName(selfField);
    }

    public String getSourceColumnName() {
        return TableColumnUtil.getRealColumnName(sourceField);
    }
}
