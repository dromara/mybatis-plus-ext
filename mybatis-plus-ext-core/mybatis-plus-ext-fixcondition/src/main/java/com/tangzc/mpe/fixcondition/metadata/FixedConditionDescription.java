package com.tangzc.mpe.fixcondition.metadata;

import com.tangzc.mpe.fixcondition.metadata.annotation.FixedCondition;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * @author don
 */
@Data
@AllArgsConstructor
public class FixedConditionDescription {

    private Class<?> entityClass;

    private Field entityField;

    private FixedCondition fixedCondition;
}
