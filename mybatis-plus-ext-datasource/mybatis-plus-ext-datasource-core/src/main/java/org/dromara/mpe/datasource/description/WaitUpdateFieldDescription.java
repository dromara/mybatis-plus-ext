package org.dromara.mpe.datasource.description;

import org.dromara.mpe.magic.util.TableColumnNameUtil;
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
        return TableColumnNameUtil.getColumnName(entityField);
    }

    public String getSourceColumnName() {
        return TableColumnNameUtil.getColumnName(sourceField);
    }
}
