package com.tangzc.mpe.autotable.strategy.pgsql.data;

import com.tangzc.mpe.autotable.strategy.CompareTableInfo;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author don
 */
@Data
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
     * 新的主键
     */
    private List<PgsqlColumnMetadata> newPrimaries = new ArrayList<>();
    /**
     * 不为空删除主键
     */
    private String dropPrimaryKeyName;

    /**
     * 注释: 需要添加/修改的字段注释《列名，注释内容》
     */
    private Map<String, String> columnComment = new HashMap<>();

    /**
     * 注释: 需要添加/修改的索引注释《索引名，注释内容》
     */
    private Map<String, String> indexComment = new HashMap<>();

    /**
     * 需要删除的列
     */
    private List<String> dropColumnList = new ArrayList<>();

    /**
     * 需要修改的列
     */
    private List<PgsqlColumnMetadata> modifyColumnMetadataList = new ArrayList<>();

    /**
     * 需要新增的列
     */
    private List<PgsqlColumnMetadata> newColumnMetadataList = new ArrayList<>();

    /**
     * 需要删除的索引
     */
    private List<String> dropIndexList = new ArrayList<>();

    /**
     * 新添加的索引
     */
    private List<PgsqlIndexMetadata> indexMetadataList = new ArrayList<>();

    @Override
    public boolean needModify() {
        return StringUtils.hasText(comment) ||
                StringUtils.hasText(dropPrimaryKeyName) ||
                !newPrimaries.isEmpty() ||
                !columnComment.isEmpty() ||
                !indexComment.isEmpty() ||
                !dropColumnList.isEmpty() ||
                !modifyColumnMetadataList.isEmpty() ||
                !newColumnMetadataList.isEmpty() ||
                !dropIndexList.isEmpty() ||
                !indexMetadataList.isEmpty();
    }

    public void addColumnComment(String columnName, String newComment) {
        this.columnComment.put(columnName, newComment);
    }

    public void addNewColumn(PgsqlColumnMetadata columnMetadata) {
        this.newColumnMetadataList.add(columnMetadata);
    }

    public void addModifyColumn(PgsqlColumnMetadata columnMetadata) {
        this.modifyColumnMetadataList.add(columnMetadata);
    }

    public void addDropColumns(Set<String> dropColumnList) {
        this.dropColumnList.addAll(dropColumnList);
    }

    public void addNewIndex(PgsqlIndexMetadata pgsqlIndexMetadata) {
        this.indexMetadataList.add(pgsqlIndexMetadata);
    }

    public void addModifyIndex(PgsqlIndexMetadata pgsqlIndexMetadata) {
        this.dropIndexList.add(pgsqlIndexMetadata.getName());
        this.indexMetadataList.add(pgsqlIndexMetadata);
    }

    public void addIndexComment(@NonNull String indexName, String newComment) {
        this.indexComment.put(indexName, newComment);
    }

    public void addDropIndexes(Set<String> indexNameList) {
        this.dropIndexList.addAll(indexNameList);
    }

    public void addNewPrimary(List<PgsqlColumnMetadata> pgsqlColumnMetadata) {
        this.newPrimaries.addAll(pgsqlColumnMetadata);
    }
}
