package org.dromara.mpe.demo.processor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface SuperMapper<T> extends BaseMapper<T> {

    default void test() {
        System.out.println("hello world");
    }
}
