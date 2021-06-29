package com.tangzc.mpe.datasource.metadata.event;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.tangzc.mpe.util.BeanClassUtil;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Getter
public class EntityUpdateEvent<E> extends ApplicationEvent {

    private final Class<E> entityClass;
    private final String entityName;
    private final E entity;
    private final Set<String> fields;

    @SafeVarargs
    private EntityUpdateEvent(E entity, SFunction<E, ?>... fields) {
        super("");
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
    public static <E> EntityUpdateEvent<E> create(E entity, SFunction<E, ?>... fields) {
        return new EntityUpdateEvent<>(entity, fields);
    }
}
