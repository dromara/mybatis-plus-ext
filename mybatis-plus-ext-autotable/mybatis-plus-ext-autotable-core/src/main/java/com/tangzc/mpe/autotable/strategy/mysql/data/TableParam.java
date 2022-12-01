package com.tangzc.mpe.autotable.strategy.mysql.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author don
 */
@Data
public class TableParam {

    /**
     * 表名
     */
    private String name;
    /**
     * 引擎
     */
    private String engine;
    /**
     * 默认字符集
     */
    private String characterSet;
    /**
     * 默认排序规则
     */
    private String collate;
    /**
     * 注释
     */
    private String comment;
    /**
     * 所有列
     */
    private List<ColumnParam> columnParamList = new ArrayList<>();
    /**
     * 索引
     */
    private List<IndexParam> indexParamList = new ArrayList<>();
}
