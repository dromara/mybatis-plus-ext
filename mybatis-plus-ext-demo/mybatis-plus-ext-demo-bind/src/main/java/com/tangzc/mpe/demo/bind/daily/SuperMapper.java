package com.tangzc.mpe.demo.bind.daily;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface SuperMapper<T> extends BaseMapper<T> {

    default void test() {
        System.out.println("hello world");
    }
}
