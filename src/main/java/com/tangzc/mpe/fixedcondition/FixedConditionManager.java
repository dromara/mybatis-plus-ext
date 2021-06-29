package com.tangzc.mpe.fixedcondition;

import com.tangzc.mpe.common.ApplicationStartListener;
import com.tangzc.mpe.fixedcondition.metadata.FixedConditionDescription;
import com.tangzc.mpe.fixedcondition.metadata.annotation.FixedCondition;
import com.tangzc.mpe.util.TableColumnUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author don
 */
@Slf4j
public class FixedConditionManager implements ApplicationStartListener.EntityFieldScanner {

    private static final Map<String, List<FixedConditionDescription>> ENTITY_LIST_MAP = new HashMap<>();

    @Override
    public void scan(Class<?> entityClass, Field field) {

        FixedCondition fixedCondition = AnnotationUtils.findAnnotation(field, FixedCondition.class);
        if (fixedCondition == null) {
            return;
        }

        String tableName = TableColumnUtil.getTableName(entityClass);

        ENTITY_LIST_MAP.computeIfAbsent(tableName, k -> new ArrayList<>())
                .add(new FixedConditionDescription(entityClass, field, fixedCondition));
    }

    public static List<FixedConditionDescription> get(String tableName){
        return ENTITY_LIST_MAP.get(tableName);
    }
}
