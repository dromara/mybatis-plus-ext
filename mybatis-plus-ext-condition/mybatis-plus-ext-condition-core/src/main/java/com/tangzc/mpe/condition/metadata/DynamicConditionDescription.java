package com.tangzc.mpe.condition.metadata;

import com.tangzc.mpe.condition.metadata.annotation.DynamicCondition;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * @author don
 */
@Data
@AllArgsConstructor
public class DynamicConditionDescription {

    private Class<?> entityClass;

    private Field entityField;

    private DynamicCondition dynamicCondition;
}
