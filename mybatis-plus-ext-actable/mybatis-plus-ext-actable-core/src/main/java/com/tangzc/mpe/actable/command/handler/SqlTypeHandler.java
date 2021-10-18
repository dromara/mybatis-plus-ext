package com.tangzc.mpe.actable.command.handler;

import com.tangzc.mpe.actable.annotation.constants.MySqlTypeConstant;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface SqlTypeHandler {

    Map<String, MySqlTypeConstant> JAVA_TO_MYSQL_TYPE_MAP = new HashMap<String, MySqlTypeConstant>() {{
        put("class java.lang.String", MySqlTypeConstant.VARCHAR);
        put("class java.lang.Long", MySqlTypeConstant.BIGINT);
        put("class java.lang.Integer", MySqlTypeConstant.INT);
        put("class java.lang.Boolean", MySqlTypeConstant.BIT);
        put("class java.math.BigInteger", MySqlTypeConstant.BIGINT);
        put("class java.lang.Float", MySqlTypeConstant.FLOAT);
        put("class java.lang.Double", MySqlTypeConstant.DOUBLE);
        put("class java.lang.Short", MySqlTypeConstant.SMALLINT);
        put("class java.math.BigDecimal", MySqlTypeConstant.DECIMAL);
        put("class java.sql.Date", MySqlTypeConstant.DATE);
        put("class java.util.Date", MySqlTypeConstant.DATETIME);
        put("class java.sql.Timestamp", MySqlTypeConstant.DATETIME);
        put("class java.sql.Time", MySqlTypeConstant.TIME);
        put("class java.time.LocalDateTime", MySqlTypeConstant.DATETIME);
        put("class java.time.LocalDate", MySqlTypeConstant.DATE);
        put("class java.time.LocalTime", MySqlTypeConstant.TIME);
        put("long", MySqlTypeConstant.BIGINT);
        put("int", MySqlTypeConstant.INT);
        put("boolean", MySqlTypeConstant.BIT);
        put("float", MySqlTypeConstant.FLOAT);
        put("double", MySqlTypeConstant.DOUBLE);
        put("short", MySqlTypeConstant.SMALLINT);
        put("char", MySqlTypeConstant.VARCHAR);
    }};

    MySqlTypeConstant getSqlType(Class<?> entityClass, Field field, Class<?> fieldType);
}
