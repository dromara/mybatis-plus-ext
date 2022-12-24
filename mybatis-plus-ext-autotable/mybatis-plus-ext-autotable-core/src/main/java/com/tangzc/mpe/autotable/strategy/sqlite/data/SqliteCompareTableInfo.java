package com.tangzc.mpe.autotable.strategy.sqlite.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author don
 */
@Data
@RequiredArgsConstructor
public class SqliteCompareTableInfo {

    /**
     * 表名: 不可变，变了意味着新表
     */
    @NonNull
    private final String name;

    /**
     * 构建表的sql，如果不为空，则重新构建表
     */
    private String rebuildTableSql;

    /**
     * 新构建索引的sql
     */
    private List<String> buildIndexSqlList = new ArrayList<>();

    /**
     * 待删除的索引
     */
    private List<String> deleteIndexList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class RebuildIndex {

        private String name;
        private String sql;
    }
}
