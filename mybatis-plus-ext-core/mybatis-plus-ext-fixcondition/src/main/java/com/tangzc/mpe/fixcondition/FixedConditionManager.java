package com.tangzc.mpe.fixcondition;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.tangzc.mpe.base.util.TableColumnUtil;
import com.tangzc.mpe.fixcondition.metadata.FixedConditionDescription;
import com.tangzc.mpe.fixcondition.metadata.annotation.FixedCondition;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author don
 */
@Slf4j
public class FixedConditionManager extends JsqlParserSupport implements InnerInterceptor {

    private static final Map<String, List<FixedConditionDescription>> ENTITY_LIST_MAP = new HashMap<>();

    public static void add(Class<?> entityClass, Field field, FixedCondition fixedCondition) {

        String tableName = TableColumnUtil.getTableName(entityClass);

        ENTITY_LIST_MAP.computeIfAbsent(tableName, k -> new ArrayList<>())
                .add(new FixedConditionDescription(entityClass, field, fixedCondition));
    }

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

        String tableName = TableColumnUtil.getTableName(delete.getTable());

        List<FixedConditionDescription> descriptions = ENTITY_LIST_MAP.get(tableName);
        if (descriptions == null) {
            return;
        }

        Expression where = getExpression(descriptions, delete.getWhere());
        delete.setWhere(where);
    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {

        String tableName = TableColumnUtil.getTableName(update.getTable());

        List<FixedConditionDescription> descriptions = ENTITY_LIST_MAP.get(tableName);
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
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                operationList.getSelects().forEach(this::processSelectBody);
            }
        }
    }

    private void processPlainSelect(PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            String tableName = TableColumnUtil.getTableName((Table) fromItem);

            List<FixedConditionDescription> descriptions = ENTITY_LIST_MAP.get(tableName);
            if (descriptions == null) {
                return;
            }
            Expression where = getExpression(descriptions, plainSelect.getWhere());
            plainSelect.setWhere(where);
        }
    }

    private Expression getExpression(List<FixedConditionDescription> descriptions, Expression where) {
        try {
            for (FixedConditionDescription description : descriptions) {
                Field entityField = description.getEntityField();
                String value = description.getFixedCondition().value();
                Class<?> type = entityField.getType();
                if (type == String.class) {
                    value = "'" + value + "'";
                }
                Expression envCondition = CCJSqlParserUtil.parseCondExpression(TableColumnUtil.getColumnName(entityField) + "=" + value);
                where = new AndExpression(where, envCondition);
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return where;
    }
}
