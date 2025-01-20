package org.dromara.mpe.bind.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.dromara.mpe.bind.metadata.JoinConditionDescription;
import org.dromara.mpe.bind.metadata.OrderByDescription;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * QueryWrapper构建工具
 *
 * @author don
 */
@NoArgsConstructor(staticName = "newInstance")
public class QueryWrapperBuilder<ENTITY> {

    private final QueryWrapper<ENTITY> queryWrapper = new QueryWrapper<>();
    private List<JoinConditionDescription> conditions;
    private String customCondition;

    private String last;

    public QueryWrapperBuilder<ENTITY> select(String[] selectColumns) {
        // 查询某些列值
        if (selectColumns != null && selectColumns.length > 0) {
            queryWrapper.select(selectColumns);
        }
        return this;
    }

    public QueryWrapperBuilder<ENTITY> where(@NonNull List<JoinConditionDescription> conditions, String customCondition) {
        // 查询某些列值
        this.conditions = conditions;
        this.customCondition = customCondition;
        return this;
    }

    public QueryWrapperBuilder<ENTITY> orderBy(List<OrderByDescription> orderBys) {
        // 查询某些列值
        for (OrderByDescription orderBy : orderBys) {
            queryWrapper.orderBy(true, orderBy.isAsc(), orderBy.getColumnName());
        }
        return this;
    }

    public QueryWrapperBuilder<ENTITY> last(String last) {
        // 查询某些列值
        this.last = last;
        return this;
    }

    public QueryWrapper<ENTITY> build(List<Where> wheres) {

        // 如果没有用户自定义条件，只有固定条件。所有固定条件之间是 OR 的关系
        if (!StringUtils.hasText(customCondition)) {
            joinCondition(queryWrapper, wheres);
        } else {
            // 存在自定义条件的情况下，由于自定义条件是固定死的，可以通用，提取出来
            // 优先根据自定义查询条件分组，同一个自定义查询条件下的 规范查询 彼此之间就是或的关系了
            Map<String, List<Where>> whereMap = wheres.stream().collect(Collectors.groupingBy(Where::getCustomCondition));

            for (Map.Entry<String, List<Where>> whereEntry : whereMap.entrySet()) {
                String customCondition = whereEntry.getKey();
                List<Where> whereList = whereEntry.getValue();
                queryWrapper.or(queryWrapper3 -> {
                    queryWrapper3.apply(customCondition);
                    joinCondition(queryWrapper3, whereList);
                });
            }
        }

        if (StringUtils.hasText(last)) {
            queryWrapper.last(last);
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
            if (values.size() > 1) {
                queryWrapper.in(column, values);
            } else {
                queryWrapper.eq(column, values.get(0));
            }
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
    public static class Where {
        private final List<WhereItem> whereItems;
        private final String customCondition;
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class WhereItem {
        private final String column;
        private final Object value;
    }
}
