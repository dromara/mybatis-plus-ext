package org.dromara.mpe.autotable;

import lombok.RequiredArgsConstructor;
import org.dromara.autotable.springboot.InitializeBeans;
import org.dromara.mpe.magic.MybatisPlusProperties;

/**
 * 实现AutoTable的接口，在AutoTable之前加载mybatis plus的配置
 */
@RequiredArgsConstructor
public class CustomInitializeBeans implements InitializeBeans {

    private final MybatisPlusProperties mybatisPlusProperties;
}
