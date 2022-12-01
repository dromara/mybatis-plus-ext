package com.tangzc.mpe.autotable.strategy.mysql.data;

/**
 * 索引方法
 *
 * @author don
 */

public enum MySqlIndexFunctionEnum {

    /**
     * 树
     */
    BTREE,
    /**
     * 哈希
     */
    HASH;

    public static MySqlIndexFunctionEnum parse(String function) {
        for (MySqlIndexFunctionEnum value : MySqlIndexFunctionEnum.values()) {
            if(value.name().equalsIgnoreCase(function)) {
                return value;
            }
        }
        throw new RuntimeException(function + "是MySql不支持的字段索引排序类型");
    }
}
