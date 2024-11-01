package org.dromara.mpe.bind;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dromara.mpe.base.ext.BindHandler;

import java.util.List;

/**
 * @author don
 */
public class MyBindHandler implements BindHandler {

    @Override
    public <BEAN> void bindOn(BEAN bind, List<SFunction<BEAN, ?>> bindFields) {
        Binder.bindOn(bind, bindFields);
    }

    @Override
    public <BEAN> void bindOn(List<BEAN> bind, List<SFunction<BEAN, ?>> bindFields) {
        Binder.bindOn(bind, bindFields);
    }

    @Override
    public <BEAN> void bindOn(IPage<BEAN> bind, List<SFunction<BEAN, ?>> bindFields) {
        Binder.bindOn(bind, bindFields);
    }
}
