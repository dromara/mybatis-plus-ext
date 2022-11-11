package com.tangzc.mpe.datasource.description;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WaitUpdateDescription {

    /**
     * 需要冗余的类
     */
    private Class<?> entityClass;

    /**
     * 数据来源的Entity class
     */
    private Class<?> sourceClass;

    /**
     * 执行更新的时候额外的where条件
     * 通常指被更新表自身的特殊条件，例如：enable=1 and is_deleted=0
     */
    private String updateCondition;

    /**
     * 冗余数据的关联条件
     */
    private List<DataSourceConditionDescription> dataSourceConditions;

    /**
     * 数据来源的Entity对应的属性
     */
    private List<WaitUpdateFieldDescription> waitUpdateFields;
}
