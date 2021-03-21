package com.tangzc.mybatis.relevance.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mybatis.common.EntityMapperManager;
import com.tangzc.mybatis.relevance.metadata.FieldDescription;
import com.tangzc.mybatis.relevance.metadata.MidConditionDescription;
import com.tangzc.mybatis.util.ColumnUtil;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

        Map<String, List<ENTITY>> entityMap = listEntitiesByCondition();

        for (BEAN bean : beans) {

            String entryGroupKey = getGroupKeyOfBean(bean, conditionSign);
            List<?> entities = entityMap.getOrDefault(entryGroupKey, Collections.emptyList());

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

    private Map<String, List<ENTITY>> listEntitiesByCondition() {

        MidConditionDescription md = conditionSign.getConditions().get(0);

        // 查询中间表
        List<Object> midDataList = listMidData();

        // 对中间表数据进行分组
        // <selfFieldVal, List<joinFieldVal>>
        Map<Object, List<Object>> midDataListMap = midDataList.stream().collect(Collectors.groupingBy(midData -> {
            try {
                return md.getSelfMidFieldGetMethod().invoke(midData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Collectors.mapping(midData -> {
            try {
                return md.getJoinMidFieldGetMethod().invoke(midData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Collectors.toList())));

        // 查询目标表数据
        List<ENTITY> entities = listEntities(md, midDataList);
        // 对join表结果进行map映射
        Map<Object, ENTITY> listMap = entities.stream().collect(Collectors.toMap(ent -> {
            try {
                return md.getJoinFieldGetMethod().invoke(ent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Function.identity()));

        // 集合中间表数据与join表数据
        Map<String, List<ENTITY>> dataMap = new HashMap<>(midDataListMap.size());
        midDataListMap.forEach((selfVal, joinValList) ->
                dataMap.put(selfVal.toString(), joinValList.stream().map(listMap::get).collect(Collectors.toList()))
        );
        return dataMap;
    }

    /**
     * 查询join表的数据
     */
    private List<ENTITY> listEntities(MidConditionDescription md, List<Object> midDataList) {

        Set<Object> joinMidFieldVals = new HashSet<>();
        try {
            for (Object midData : midDataList) {
                joinMidFieldVals.add(md.getJoinMidFieldGetMethod().invoke(midData));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        QueryWrapper<ENTITY> queryWrapper = new QueryWrapper<>();
        // 查询某些列值
        String[] selectColumns = fillDataCallback.selectColumns(beans, conditionSign, fieldDescriptions);
        if (selectColumns != null && selectColumns.length > 0) {
            queryWrapper.select(selectColumns);
        }
        queryWrapper.in(ColumnUtil.humpToLine(md.getJoinField()), joinMidFieldVals);
        return EntityMapperManager.getMapper(conditionSign.getJoinEntityClass())
                .selectList(queryWrapper);
    }

    /**
     * 查询中间表的数据
     */
    private <MID> List<MID> listMidData() {

        MidConditionDescription midConditionDescription = conditionSign.getConditions().get(0);
        QueryWrapper<MID> queryWrapper = new QueryWrapper<>();
        // 查询某些列值
        queryWrapper.select(ColumnUtil.humpToLine(midConditionDescription.getJoinMidField()),
                ColumnUtil.humpToLine(midConditionDescription.getSelfMidField()));
        List<Object> selfFieldVals = new ArrayList<>();
        try {
            for (BEAN bean : beans) {
                selfFieldVals.add(midConditionDescription.getSelfFieldGetMethod().invoke(bean));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        queryWrapper.in(ColumnUtil.humpToLine(midConditionDescription.getSelfMidField()), selfFieldVals);

        BaseMapper<MID> baseMapper = EntityMapperManager.getMapper((Class<MID>) midConditionDescription.getMidEntity());
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

    private String getGroupKeyOfBean(BEAN bean, ConditionSign<ENTITY, MidConditionDescription> conditionSign) {

        MidConditionDescription midConditionDescription = conditionSign.getConditions().get(0);

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
