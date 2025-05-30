package org.dromara.mpe.magic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mpe.magic.util.SpringContextUtil;

/**
 * @author don
 */
@Slf4j
public class MapperExecuter {

    public static <ENTITY, R> R getMapperExecute(Class<ENTITY> entityClass, SFunction<BaseMapper<ENTITY>, R> sFunction) {
        try {
            // 如果mapper存在spring中，则使用spring中的mapper，自带多数据源切换等策略
            TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
            Class<? extends BaseMapper<ENTITY>> mapperClass = (Class<? extends BaseMapper<ENTITY>>) ClassUtils.toClassConfident(tableInfo.getCurrentNamespace());
            BaseMapper<ENTITY> springMapper = SpringContextUtil.getBeanOfType(mapperClass);
            return sFunction.apply(springMapper);
        } catch (Exception ignore) {
            return SqlHelper.execute(entityClass, sFunction);
        }
    }
}
