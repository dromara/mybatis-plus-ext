package org.dromara.mpe.bind.binder;

import org.dromara.mpe.bind.metadata.FieldDescription;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IBinder<BEAN, FD extends FieldDescription<? extends Annotation, CONDITION_DESC>, CONDITION_DESC> {

    /**
     * 绑定操作
     *
     * @param beans
     * @param fieldDescriptions
     */
    default void doBind(List<BEAN> beans, List<FD> fieldDescriptions) {

        if (fieldDescriptions.isEmpty()) {
            return;
        }

        // 合并相同条件的关联，减少查询
        Map<FieldDescription.ConditionSign<?, CONDITION_DESC>, List<FD>> fieldDescriptionsGroupByCondition =
                fieldDescriptions.stream().collect(Collectors.groupingBy(fd -> fd.conditionUniqueKey()));

        // 填充数据
        fieldDescriptionsGroupByCondition.forEach(
                (entityJoinCondition, fieldAnnotationList) -> fillData(beans, entityJoinCondition, fieldAnnotationList)
        );
    }

    /**
     * 批量查询数据做绑定
     *
     * @param beans               待填充的集合
     * @param entityJoinCondition 分组条件
     * @param fieldAnnotationList 相同条件的字段集合
     * @param <ENTITY>            关联的对象类型
     */
    <ENTITY> void fillData(List<BEAN> beans, FieldDescription.ConditionSign<ENTITY, CONDITION_DESC> entityJoinCondition, List<FD> fieldAnnotationList);
}
