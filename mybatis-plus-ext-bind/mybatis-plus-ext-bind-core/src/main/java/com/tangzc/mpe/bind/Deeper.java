package com.tangzc.mpe.bind;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据级联处理
 *
 * @author don
 */
@Slf4j
public class Deeper<BEAN> extends ArrayList<BEAN> {

    private Deeper(List<BEAN> beans) {
        super(beans);
    }

    public static <BEAN> Deeper<BEAN> with(BEAN bean) {
        if(bean == null) {
            return new Deeper<>(Collections.emptyList());
        }
        return new Deeper<>(Collections.singletonList(bean));
    }

    public static <BEAN> Deeper<BEAN> with(List<BEAN> beans) {
        if(beans == null) {
            return new Deeper<>(Collections.emptyList());
        }
        return new Deeper<>(beans);
    }

    public static <BEAN> Deeper<BEAN> with(IPage<BEAN> beans) {
        return new Deeper<>(beans.getRecords());
    }

    public <BEAN2> Deeper<BEAN2> in(SFunction<BEAN, BEAN2> fieldFunc) {

        return new Deeper<>(
                this.stream()
                        .map(fieldFunc)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    public <BEAN2> Deeper<BEAN2> inList(SFunction<BEAN, List<BEAN2>> fieldFunc) {

        return new Deeper<>(
                this.stream()
                        .map(fieldFunc)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
    }
}