package com.tangzc.mpe.autotable.utils;

import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
public class IndexRepeatChecker {

    // 标记所有的索引，用于检测重复的
    private Set<String> exitsIndexes = new HashSet<>(16);

    // 索引重复检测过滤器
    public boolean filter(String name) {

        boolean exits = exitsIndexes.contains(name);
        if (exits) {
            throw new RuntimeException("发现重复索引:" + name);
        } else {
            exitsIndexes.add(name);
        }

        return true;
    }

    ;
}
