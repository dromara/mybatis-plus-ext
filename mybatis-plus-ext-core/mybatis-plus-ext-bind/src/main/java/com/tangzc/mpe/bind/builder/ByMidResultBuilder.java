package com.tangzc.mpe.bind.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.MapperScanner;
import com.tangzc.mpe.bind.metadata.FieldDescription;
import com.tangzc.mpe.bind.metadata.MidConditionDescription;
import com.tangzc.mpe.bind.metadata.OrderByDescription;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author don
 */
@AllArgsConstructor(staticName = "newInstance")
public class ByMidResultBuilder<BEAN, ENTITY> {

    private final List<BEAN> beans;
    private final ConditionSign<ENTITY, MidConditionDescription> conditionSign;
    private final List<? extends FieldDescription<?, MidConditionDescription>> fieldDescriptions;
    private final FillDataCallback fillDataCallback;

    public void fillData() {

        // 中间表关联关系，仅限于一个条件，所以直接get(0)
        MidConditionDescription midConditionDescription = conditionSign.getConditions().get(0);

        // <selfField值, List<join表数据>>
        Map<String, List<ENTITY>> joinEntityMap = listEntitiesByCondition(midConditionDescription);

        // 循环填充每个bean
        for (BEAN bean : beans) {

            String selfFieldVal = getSelfFieldValFromBean(midConditionDescription, bean);
            List<?> entities = joinEntityMap.getOrDefault(selfFieldVal, Collections.emptyList());

            // 循环填充bean中的每个属性
            for (FieldDescription<?, MidConditionDescription> fieldAnnotation : fieldDescriptions) {

                List<?> dataList = entities;
                if (fillDataCallback != null) {
                    fillDataCallback.fillBefore(bean, fieldAnnotation, dataList);
                    dataList = fillDataCallback.changeDataList(bean, fieldAnnotation, dataList);
                }

                fullDataToBeanField(bean, fieldAnnotation, dataList);
            }
        }
    }

    /**
     * 查询目标表的数据集合
     *
     * @return <selfField值, List<join表数据>>
     */
    private Map<String, List<ENTITY>> listEntitiesByCondition(MidConditionDescription midConditionDescription) {

        // 查询中间表符合要求的数据
        List<Object> midDataList = listMidData(midConditionDescription);

        // 对中间表数据进行分组，确定主表与从表的数据对应关系
        // <selfFieldVal, List<joinFieldVal>>
        Map<Object, List<Object>> midDataListMap = midDataList.stream().collect(Collectors.groupingBy(midData -> {
            try {
                return midConditionDescription.getSelfMidFieldGetMethod().invoke(midData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Collectors.mapping(midData -> {
            try {
                return midConditionDescription.getJoinMidFieldGetMethod().invoke(midData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Collectors.toList())));

        // 查询目标表数据
        Set<Object> joinMidFieldVals = midDataListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        List<ENTITY> entities = listEntities(midConditionDescription, joinMidFieldVals);
        // 对join表结果进行map映射
        Map<Object, ENTITY> entitiesMap = entities.stream().collect(Collectors.toMap(ent -> {
            try {
                return midConditionDescription.getJoinFieldGetMethod().invoke(ent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Function.identity()));

        // 集合中间表数据与join表数据
        Map<String, List<ENTITY>> dataMap = new HashMap<>(midDataListMap.size());
        midDataListMap.forEach((selfVal, joinValList) ->
                dataMap.put(selfVal.toString(),
                        joinValList.stream().map(entitiesMap::get)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                )
        );
        return dataMap;
    }

    /**
     * 查询join表的数据
     */
    private List<ENTITY> listEntities(MidConditionDescription midConditionDescription, Set<Object> joinMidFieldVals) {

        if (joinMidFieldVals.isEmpty()) {
            return Collections.emptyList();
        }

        QueryWrapper<ENTITY> queryWrapper = new QueryWrapper<>();
        // 查询某些列值
        String[] selectColumns = fillDataCallback.selectColumns(beans, conditionSign, fieldDescriptions);
        if (selectColumns != null && selectColumns.length > 0) {
            queryWrapper.select(selectColumns);
        }
        // 添加主要条件
        queryWrapper.in(midConditionDescription.getJoinColumnName(), joinMidFieldVals);
        // 自定义条件
        queryWrapper.apply(StringUtils.hasText(conditionSign.getCustomCondition()), conditionSign.getCustomCondition());
        // 排序
        for (OrderByDescription orderBy : conditionSign.getOrderBys()) {
            queryWrapper.orderBy(true, orderBy.isAsc(), orderBy.getColumnName());
        }
        // last sql
        queryWrapper.last(conditionSign.getLast());

        return MapperScanner.getMapper(conditionSign.getJoinEntityClass())
                .selectList(queryWrapper);
    }

    /**
     * 查询中间表的数据
     */
    private <MID> List<MID> listMidData(MidConditionDescription midConditionDescription) {

        // 获取bean集合中自身字段值的集合
        List<Object> selfFieldVals = new ArrayList<>();
        try {
            for (BEAN bean : beans) {
                selfFieldVals.add(midConditionDescription.getSelfFieldGetMethod().invoke(bean));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (selfFieldVals.isEmpty()) {
            return Collections.emptyList();
        }

        // 构建查询器
        QueryWrapper<MID> queryWrapper = new QueryWrapper<>();
        // 仅仅查询关联关系的两列（对于性能提升只在中间表列数很多的情况下有意义）
        queryWrapper.select(midConditionDescription.getJoinMidColumnName(), midConditionDescription.getSelfMidColumnName());
        queryWrapper.in(midConditionDescription.getSelfMidColumnName(), selfFieldVals);

        BaseMapper<MID> baseMapper = MapperScanner.getMapper((Class<MID>) midConditionDescription.getMidEntity());

        return baseMapper.selectList(queryWrapper);
    }

    private void fullDataToBeanField(BEAN bean,
                                     FieldDescription<?, MidConditionDescription> fieldAnnotation,
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
            e.printStackTrace();
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

    private String getSelfFieldValFromBean(MidConditionDescription midConditionDescription, BEAN bean) {

        try {
            Object val = midConditionDescription.getSelfFieldGetMethod().invoke(bean);
            return Optional.ofNullable(val).map(Object::toString).orElse("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface FillDataCallback {

        /**
         * 只查询指定的字段，null或者长度0查询全部
         */
        default String[] selectColumns(List<?> beans,
                                       ConditionSign<?, MidConditionDescription> conditionSign,
                                       List<? extends FieldDescription<?, MidConditionDescription>> fieldAnnotationList) {
            return null;
        }

        /**
         * 查询到结果之后，填充数据之前回调
         */
        default void fillBefore(Object bean, FieldDescription<?, MidConditionDescription> fieldAnnotation, List<?> entities) {

        }

        /**
         * 查询到结果之后，填充数据之前回调，该回调可以修改填充到Bean的结果
         */
        default List<?> changeDataList(Object bean, FieldDescription<?, MidConditionDescription> fieldAnnotation, List<?> entities) {
            return entities;
        }
    }
}
