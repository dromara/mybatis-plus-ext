package com.tangzc.mpe.autotable.constants;

/**
 * @author don
 */
public interface ISqlColumnType {

    /**
     * 类型名称
     * @return 名称
     */
    String name();

    /**
     * 默认类型长度
     * @return 长度
     */
    Integer getLengthDefault();

    /**
     * 默认小数点后长度
     * @return 长度
     */
    Integer getDecimalLengthDefault();
}
