package org.dromara.mpe.autotable;

import org.dromara.autotable.adapter.mybatisplus.MybatisPlusAutoTableClassScanner;
import org.dromara.mpe.autotable.annotation.Table;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ext 类扫描器，继承 MybatisPlusAutoTableClassScanner。
 * <p>
 * 在父类已包含的 {AutoTable.class, TableName.class} 基础上，
 * 追加 ext @Table 注解，使扫描器能发现仅标注 @Table 的实体类。
 *
 * @author don
 */
public class ExtClassScanner extends MybatisPlusAutoTableClassScanner {

    @Override
    protected Set<Class<? extends Annotation>> getIncludeAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>(super.getIncludeAnnotations());
        annotations.add(Table.class);
        return annotations;
    }
}
