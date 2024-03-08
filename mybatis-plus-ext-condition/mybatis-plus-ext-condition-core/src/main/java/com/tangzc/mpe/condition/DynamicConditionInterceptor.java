package com.tangzc.mpe.condition;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.tangzc.mpe.condition.metadata.DynamicConditionDescription;
import com.tangzc.mpe.condition.metadata.IDynamicConditionHandler;
import com.tangzc.mpe.magic.util.TableColumnNameUtil;
import com.tangzc.mpe.magic.util.EnumUtil;
import com.tangzc.mpe.magic.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class DynamicConditionInterceptor extends JsqlParserSupport implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(this.changeSql(mpBs.sql()));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(this.changeSql(mpBs.sql()));
        }
    }

    protected String changeSql(String sql) {

        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            return processParser(statement, 0, sql, null);
        } catch (JSQLParserException ignore) {
        }
        return sql;
    }

    @Override
    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        // 无需操作
    }

    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {

        String tableName = TableColumnNameUtil.filterSpecialChar(delete.getTable().getName());

        List<DynamicConditionDescription> descriptions = DynamicConditionManager.getDynamicCondition(tableName);
        if (descriptions == null) {
            return;
        }

        Expression where = getExpression(descriptions, delete.getWhere());
        delete.setWhere(where);
    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {

        String tableName = TableColumnNameUtil.filterSpecialChar(update.getTable().getName());

        List<DynamicConditionDescription> descriptions = DynamicConditionManager.getDynamicCondition(tableName);
        if (descriptions == null) {
            return;
        }

        Expression where = getExpression(descriptions, update.getWhere());
        update.setWhere(where);
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        processSelectBody(select.getSelectBody());
    }

    private void processSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSubSelect().getSelectBody() != null) {
                processSelectBody(withItem.getSubSelect().getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && !operationList.getSelects().isEmpty()) {
                operationList.getSelects().forEach(this::processSelectBody);
            }
        }
    }

    private void processPlainSelect(PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            String tableName = TableColumnNameUtil.filterSpecialChar(((Table) fromItem).getName());

            List<DynamicConditionDescription> descriptions = DynamicConditionManager.getDynamicCondition(tableName);
            if (descriptions == null) {
                return;
            }
            Expression where = getExpression(descriptions, plainSelect.getWhere());
            plainSelect.setWhere(where);
        }
    }

    private Expression getExpression(List<DynamicConditionDescription> descriptions, Expression where) {
        try {
            for (DynamicConditionDescription description : descriptions) {
                Field entityField = description.getEntityField();
                Class<? extends IDynamicConditionHandler> handlerClass = description.getDynamicCondition().value();
                IDynamicConditionHandler conditionHandler = SpringContextUtil.getBeanOfType(handlerClass);

                // 表示该条件跳过，不参与过滤
                if (!conditionHandler.enable()) {
                    continue;
                }

                String condExpr;
                List<Object> values = conditionHandler.values();
                if (values == null || values.isEmpty()) {
                    condExpr = TableColumnNameUtil.getRealColumnName(entityField) + " is null";
                } else {
                    // 字符串的话，两边追加'
                    values = autoFillStrVal(entityField, values);
                    if (values.size() == 1) {
                        condExpr = TableColumnNameUtil.getRealColumnName(entityField) + "=" + values.get(0) + "";
                    } else {
                        condExpr = TableColumnNameUtil.getRealColumnName(entityField) + " in(" + values.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
                    }
                }
                Expression envCondition = CCJSqlParserUtil.parseCondExpression(condExpr);
                if (where == null) {
                    where = envCondition;
                } else {
                    where = new AndExpression(where, envCondition);
                }
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return where;
    }

    /**
     * 字符串的话，两边追加'
     */
    private static List<Object> autoFillStrVal(Field entityField, List<Object> values) {
        Class<?> type = entityField.getType();
        if (type.isEnum()) {
            type = EnumUtil.getEnumFieldSaveDbType(type);
        }
        if (type == String.class) {
            values = values.stream()
                    .map(value -> {
                        String valStr = value.toString();
                        if (valStr.startsWith("'") && valStr.endsWith("'")) {
                            return valStr;
                        }
                        return "'" + valStr + "'";
                    })
                    .collect(Collectors.toList());
        }
        return values;
    }
}
