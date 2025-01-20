package org.dromara.mpe.bind.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.dromara.mpe.base.MapperScanner;
import org.dromara.mpe.bind.CollectionSplitter;
import org.dromara.mpe.bind.metadata.FieldDescription;
import org.dromara.mpe.bind.metadata.JoinConditionDescription;
import org.dromara.mpe.bind.parser.CustomConditionParser;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author don
 */
@AllArgsConstructor(staticName = "newInstance")
public class ResultBuilder<BEAN, ENTITY> {

    /**
     * 待填充数据的原始集合
     */
    private final List<BEAN> beans;
    /**
     * 某一分组依据：根据BEAN上的所有绑定关系，进行分组的条件
     */
    private final FieldDescription.ConditionSign<ENTITY, JoinConditionDescription> conditionSign;
    /**
     * 某一条件分组下的所有字段描述
     */
    private final List<? extends FieldDescription<?, JoinConditionDescription>> fieldDescriptions;
    /**
     * 数据填充前自定义回调
     */
    private final FillDataCallback fillDataCallback;

    public void fillData() {

        // <条件签名, List<数据>>
        Map<String, List<ENTITY>> entityMap = listEntitiesByCondition();

        // 循环填充每个bean
        for (BEAN bean : beans) {

            String entryGroupKey = getConditionSignatureBySelf(bean, conditionSign);
            List<?> entities = entityMap.getOrDefault(entryGroupKey, Collections.emptyList());

            // 循环填充bean中的每个属性
            for (FieldDescription<?, JoinConditionDescription> fieldAnnotation : fieldDescriptions) {

                List<?> dataList = entities;
                if (fillDataCallback != null) {
                    fillDataCallback.fillBefore(bean, fieldAnnotation, dataList);
                    dataList = fillDataCallback.changeDataList(bean, fieldAnnotation, dataList);
                }

                fullDataToBeanField(bean, fieldAnnotation, dataList);
            }
        }
    }

    private Map<String, List<ENTITY>> listEntitiesByCondition() {

        String lastSql = conditionSign.getLast();

        List<ENTITY> entities = new ArrayList<>();
        // 存在last sql的情况下，目前发现的就是limit 1，而limit 1这种情况无法合并查询，需要单独查询，然后合并结果
        if (StringUtils.hasText(lastSql)) {
            Set<ENTITY> norepeatEntities = new HashSet<>();
            for (BEAN bean : beans) {
                List<BEAN> beanList = Collections.singletonList(bean);
                norepeatEntities.addAll(listEntitiesByCondition(beanList));
            }
            entities.addAll(norepeatEntities);
        } else {
            entities = listEntitiesByCondition(beans);
        }

        return entities.stream()
                .collect(Collectors.groupingBy(
                        entity -> getConditionSignatureByJoin(entity, conditionSign)
                ));
    }

    private List<ENTITY> listEntitiesByCondition(List<BEAN> beanList) {

        // 提取bean集合中的所有条件
        Set<QueryWrapperBuilder.Where> whereSet = new HashSet<>();
        // 汇总所有对象上的查询条件
        for (BEAN bean : beanList) {
            List<QueryWrapperBuilder.WhereItem> whereItemList = new ArrayList<>();
            for (JoinConditionDescription condition : conditionSign.getConditions()) {
                try {
                    String column = condition.getJoinColumnName();
                    Object val = condition.getSelfFieldGetMethod().invoke(bean);
                    whereItemList.add(new QueryWrapperBuilder.WhereItem(column, val));
                } catch (Exception e) {
                    throw new RuntimeException("获取字段" + condition.getSelfFieldName() + "的值失败。", e);
                }
            }
            whereSet.add(new QueryWrapperBuilder.Where(whereItemList, CustomConditionParser.parse(bean, conditionSign.getCustomCondition())));
        }

        // 拆分查询条件，防止条件过多导致sql中in条件内容过长
        List<List<QueryWrapperBuilder.Where>> splitWhereList = CollectionSplitter.splitList(whereSet, 500);

        return splitWhereList.parallelStream().map(wheres -> {
                    QueryWrapper<ENTITY> queryWrapper = QueryWrapperBuilder.<ENTITY>newInstance()
                            .select(fillDataCallback.selectColumns(beans, conditionSign, fieldDescriptions))
                            .where(conditionSign.getConditions(), conditionSign.getCustomCondition())
                            .orderBy(conditionSign.getOrderBys())
                            .last(conditionSign.getLast())
                            .build(wheres);

                    Class<ENTITY> joinEntityClass = conditionSign.getJoinEntityClass();
                    return MapperScanner.getMapperExecute(joinEntityClass, mapper -> mapper.selectList(queryWrapper));
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void fullDataToBeanField(BEAN bean,
                                     FieldDescription<?, JoinConditionDescription> fieldAnnotation,
                                     List<?> dataList) {

        Object val;

        if (fieldAnnotation.isCollection()) {
            val = dataList;
        } else {
            // 异常情况检查
            checkBindDataSize(bean.getClass().getName(), dataList.size(), fieldAnnotation.getFieldName());
            val = dataList.isEmpty() ? null : dataList.get(0);
        }

        try {
            fieldAnnotation.getSetMethod().invoke(bean, val);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("字段" + fieldAnnotation.getFieldName() + "设置值失败。", e);
        }
    }

    /**
     * 检查绑定的数据结果，如果定义的类型为对象，但是却查出了多条数据，则报错
     */
    private void checkBindDataSize(String beanName, int dataListSize, String fieldName) {

        if (dataListSize > 1) {
            String entityName = conditionSign.getJoinEntityClass().getName();
            throw new RuntimeException(beanName + "." + fieldName
                    + "关联" + entityName + "的数据条数大于1条");
        }
    }

    private String getConditionSignatureBySelf(BEAN bean, FieldDescription.ConditionSign<ENTITY, JoinConditionDescription> conditionSign) {

        return conditionSign.getConditions().stream()
                .map(condition -> {
                    try {
                        return condition.getJoinFieldName() + "=" + condition.getSelfFieldGetMethod().invoke(bean);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.joining("|"));
    }

    private String getConditionSignatureByJoin(ENTITY entity, FieldDescription.ConditionSign<ENTITY, JoinConditionDescription> conditionSign) {

        return conditionSign.getConditions().stream()
                .map(condition -> {
                    try {
                        return condition.getJoinFieldName() + "=" + condition.getJoinFieldGetMethod().invoke(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.joining("|"));
    }

    public interface FillDataCallback {

        /**
         * 只查询指定的字段，null或者长度0查询全部
         */
        default String[] selectColumns(List<?> beans,
                                       FieldDescription.ConditionSign<?, JoinConditionDescription> entityJoinCondition,
                                       List<? extends FieldDescription<?, JoinConditionDescription>> fieldAnnotationList) {
            return null;
        }

        /**
         * 查询到结果之后，填充数据之前回调
         */
        default void fillBefore(Object bean, FieldDescription<?, JoinConditionDescription> fieldAnnotation, List<?> entities) {

        }

        /**
         * 查询到结果之后，填充数据之前回调，该回调可以修改填充到Bean的结果
         */
        default List<?> changeDataList(Object bean, FieldDescription<?, JoinConditionDescription> fieldAnnotation, List<?> entities) {
            return entities;
        }
    }
}
