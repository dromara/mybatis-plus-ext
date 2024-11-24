package org.dromara.mpe.base.ext;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.List;

public interface BindHandler {

    <BEAN> void bindOn(BEAN bind, List<SFunction<BEAN, ?>> bindFields);

    <BEAN> void bindOn(List<BEAN> bind, List<SFunction<BEAN, ?>> bindFields);

    <BEAN> void bindOn(IPage<BEAN> bind, List<SFunction<BEAN, ?>> bindFields);
}
