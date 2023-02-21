package com.tangzc.mpe.autotable.strategy;

/**
 * 比对表与实体的数据模型接口
 *
 * @author don
 */
public interface CompareTableInfo {

    /**
     * 是否需要修改表,即表与模型是否存在差异
     */
    boolean needModify();
}
