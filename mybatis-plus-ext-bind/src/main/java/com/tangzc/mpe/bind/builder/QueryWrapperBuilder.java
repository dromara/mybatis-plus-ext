package com.tangzc.mpe.bind.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tangzc.mpe.bind.metadata.JoinConditionDescription;
import com.tangzc.mpe.bind.metadata.OrderByDescription;
import com.tangzc.mpe.bind.parser.CustomConditionParser;
import com.tangzc.mpe.core.util.TableColumnUtil;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author don
 */
@NoArgsConstructor(staticName = "newInstance")
public class QueryWrapperBuilder<ENTITY> {

    private final QueryWrapper<ENTITY> queryWrapper = new QueryWrapper<>();
    private List<JoinConditionDescription> conditions;
    private String customCondition;

    public QueryWrapperBuilder<ENTITY> select(String[] selectColumns) {
        // 查询某些列值
        if (selectColumns != null && selectColumns.length > 0) {
            queryWrapper.select(selectColumns);
        }
        return this;
    }

    public QueryWrapperBuilder<ENTITY> where(List<JoinConditionDescription> conditions, String customCondition) {
        // 查询某些列值
        this.conditions = conditions;
        this.customCondition = customCondition;
        return this;
    }

    public QueryWrapperBuilder<ENTITY> orderBy(List<OrderByDescription> orderBys) {
        // 查询某些列值
        for (OrderByDescription orderBy : orderBys) {
            queryWrapper.orderBy(true, orderBy.isAsc(), TableColumnUtil.humpToLine(orderBy.getField()));
        }
        return this;
    }

    public <BEAN> QueryWrapper<ENTITY> build(List<BEAN> beans) {

        // 提取bean集合中的所有条件
        Set<Where> whereSet = new HashSet<>();
        if (!conditions.isEmpty()) {
            // 汇总所有对象上的查询条件
            for (BEAN bean : beans) {
                List<WhereItem> whereItemList = new ArrayList<>();
                for (JoinConditionDescription condition : conditions) {
                    String column = TableColumnUtil.humpToLine(condition.getJoinField());
                    try {
                        Object val = condition.getSelfFieldGetMethod().invoke(bean);
                        whereItemList.add(new WhereItem(column, val));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                whereSet.add(new Where(whereItemList, CustomConditionParser.parse(bean, customCondition)));
            }
        }

        // 如果没有用户自定义条件，只有固定条件。所有固定条件之间是 OR 的关系
        if (StringUtils.isEmpty(customCondition)) {
            joinCondition(queryWrapper, whereSet);
        } else {
            // 优先根据自定义查询条件分组，同一个自定义查询条件下的 规范查询 彼此之间就是或的关系了
            Map<String, List<Where>> whereMap = whereSet.stream().collect(Collectors.groupingBy(Where::getCustomCondition));

            for (Map.Entry<String, List<Where>> whereEntry : whereMap.entrySet()) {
                String customCondition = whereEntry.getKey();
                List<Where> whereList = whereEntry.getValue();
                queryWrapper.or(queryWrapper3 -> {
                    queryWrapper3.apply(customCondition);
                    joinCondition(queryWrapper3, whereList);
                });
            }
        }

        return queryWrapper;
    }

    private void joinCondition(QueryWrapper<ENTITY> queryWrapper, Collection<Where> wheres) {

        // 如果固定条件 只有一个，可以优化为in的查询方式
        if (conditions.size() == 1) {
            List<WhereItem> whereItemList = wheres.stream()
                    .map(Where::getWhereItems)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            String column = whereItemList.get(0).column;
            List<Object> values = whereItemList.stream()
                    .map(WhereItem::getValue)
                    .collect(Collectors.toList());
            queryWrapper.in(column, values);
        } else {
            queryWrapper.and(queryWrapper2 -> {
                for (Where where : wheres) {
                    queryWrapper2.or(qw -> {
                        for (WhereItem whereItem : where.whereItems) {
                            qw.eq(whereItem.column, whereItem.value);
                        }
                    });
                }
            });
        }
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class Where {
        private final List<WhereItem> whereItems;
        private final String customCondition;
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class WhereItem {
        private final String column;
        private final Object value;
    }
}
