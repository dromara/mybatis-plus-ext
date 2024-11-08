package com.tangzc.mpe.autotable.annotation;

public enum GenerateMode {
    /**
     * 默认模式，遵从mybatis-plus-ext.config的全局配置
     */
    AUTO,
    /**
     * 忽略全局配置，生成
     */
    YES,
    /**
     * 忽略全局配置，不生成
     */
    NO
}
