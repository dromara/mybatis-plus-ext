package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.annotation.TableName;
import org.dromara.autotable.core.AutoTableClassScanner;
import org.dromara.mpe.autotable.annotation.Table;

import java.lang.annotation.Annotation;
import java.util.Set;

public class CustomAutoTableClassScanner extends AutoTableClassScanner {
    @Override
    protected Set<Class<? extends Annotation>> getIncludeAnnotations() {
        Set<Class<? extends Annotation>> includeAnnotations = super.getIncludeAnnotations();
        includeAnnotations.add(Table.class);
        includeAnnotations.add(TableName.class);
        return includeAnnotations;
    }
}
