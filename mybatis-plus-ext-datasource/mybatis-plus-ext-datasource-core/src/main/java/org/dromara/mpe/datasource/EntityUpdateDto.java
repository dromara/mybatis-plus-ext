package org.dromara.mpe.datasource;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import org.dromara.mpe.magic.util.BeanClassUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据更新事件
 *
 * @author don
 */
@Getter
public class EntityUpdateDto<E> {

    /**
     * 需要冗余的类
     */
    private final Class<E> entityClass;
    private final String entityName;
    private final E entity;
    private final Set<String> fields;

    @SafeVarargs
    private EntityUpdateDto(E entity, SFunction<E, ?>... fields) {
        this.entityClass = (Class<E>) entity.getClass();
        this.entityName = entityClass.getName();
        this.entity = entity;
        if (fields == null) {
            this.fields = Collections.emptySet();
        } else {
            this.fields = Arrays.stream(fields).map(BeanClassUtil::getFieldName).collect(Collectors.toSet());
        }
    }

    @SafeVarargs
    public static <E> EntityUpdateDto<E> create(E entity, SFunction<E, ?>... fields) {
        return new EntityUpdateDto<>(entity, fields);
    }
}
