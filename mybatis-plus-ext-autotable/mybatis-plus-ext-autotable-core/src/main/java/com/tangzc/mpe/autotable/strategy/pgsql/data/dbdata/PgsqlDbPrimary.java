package com.tangzc.mpe.autotable.strategy.pgsql.data.dbdata;

import lombok.Data;

/**
 * pgsql数据库，索引信息
 */
@Data
public class PgsqlDbPrimary {

    /**
     * 主键名称
     */
    private String primaryName;
    /**
     * 主键列的拼接,例子：name,phone
     */
    private String columns;
}
