package org.dromara.mpe.demo.bind;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface CustomBaseMapper<M extends BaseMapper<T>, T> extends BaseMapper<T> {

}
