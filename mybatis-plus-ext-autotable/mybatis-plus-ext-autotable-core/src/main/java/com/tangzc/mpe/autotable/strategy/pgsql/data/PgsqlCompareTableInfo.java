package com.tangzc.mpe.autotable.strategy.pgsql.data;

import com.tangzc.mpe.autotable.strategy.CompareTableInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author don
 */
@RequiredArgsConstructor
public class PgsqlCompareTableInfo implements CompareTableInfo {

    /**
     * 表名: 不可变，变了意味着新表
     */
    @NonNull
    private final String name;

    /**
     * 注释: 有值说明需要改
     */
    private String comment;

    /**
     * 注释: 需要添加/修改的字段注释《字段名，注释内容》
     */
    private Map<String, String> fieldComment;

    /**
     * 注释: 需要添加/修改的索引注释《索引名，注释内容》
     */
    private Map<String, String> indexComment;

    /**
     * 需要删除的列
     */
    private List<String> dropColumnList;

    /**
     * 需要修改的列
     */
    private List<PgsqlColumnMetadata> modifyColumnMetadataList;

    /**
     * 需要删除的索引
     */
    private List<String> dropIndexList;

    /**
     * 新添加的索引
     */
    private List<PgsqlIndexMetadata> indexMetadataList;

    @Override
    public boolean needModify() {
        return false;
    }
}
