package com.tangzc.mpe.autotable.strategy.mysql.data;

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
public class MysqlCompareTableInfo {

    /**
     * 表名: 不可变，变了意味着新表
     */
    @NonNull
    private final String name;
    /**
     * 引擎: 有值，则说明需要修改
     */
    private String engine;
    /**
     * 默认字符集: 有值，则说明需要修改
     */
    private String characterSet;
    /**
     * 默认排序规则: 有值，则说明需要修改
     */
    private String collate;
    /**
     * 注释: 有值，则说明需要修改
     */
    private String comment;
    /**
     * 重新设置主键
     */
    private boolean resetPrimary;
    /**
     * 删除的列：谨慎，会导致数据丢失
     */
    private final List<String> dropColumnList = new ArrayList<>();
    /**
     * 修改的列
     */
    private final List<MysqlColumnMetadata> modifyMysqlColumnMetadataList = new ArrayList<>();
    /**
     * 新增的列
     */
    private final List<MysqlColumnMetadata> mysqlColumnMetadataList = new ArrayList<>();
    /**
     * 删除的索引
     */
    private final List<String> dropIndexList = new ArrayList<>();
    /**
     * 索引
     */
    private final List<MysqlIndexMetadata> mysqlIndexMetadataList = new ArrayList<>();

    /**
     * 判断该修改参数，是不是可用，如果除了name，其他值均没有设置过，则无效，反之有效
     */
    public boolean isValid() {
        return engine != null ||
                characterSet != null ||
                collate != null ||
                comment != null ||
                resetPrimary ||
                !dropColumnList.isEmpty() ||
                !modifyMysqlColumnMetadataList.isEmpty() ||
                !mysqlColumnMetadataList.isEmpty() ||
                !dropIndexList.isEmpty() ||
                !mysqlIndexMetadataList.isEmpty();
    }
}
