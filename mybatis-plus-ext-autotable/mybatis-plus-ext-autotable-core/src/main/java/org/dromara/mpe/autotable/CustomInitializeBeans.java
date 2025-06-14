package org.dromara.mpe.autotable;

import lombok.RequiredArgsConstructor;
import org.dromara.autotable.springboot.InitializeBeans;
import org.dromara.mpe.magic.MybatisPlusProperties;

@RequiredArgsConstructor
public class CustomInitializeBeans implements InitializeBeans {

    private final MybatisPlusProperties mybatisPlusProperties;
}
