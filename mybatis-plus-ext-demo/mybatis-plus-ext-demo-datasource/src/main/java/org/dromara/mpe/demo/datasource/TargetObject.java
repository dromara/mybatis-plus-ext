package org.dromara.mpe.demo.datasource;

import lombok.Data;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.datasource.annotation.Condition;
import org.dromara.mpe.datasource.annotation.DataSource;

@Data
@Table(comment = "目标表")
public class TargetObject {

    private String id;
    private String name;

    private String sourceId;

    @DataSource(source = SourceObject.class, field = SourceObjectDefine.name, conditions = @Condition(selfField = TargetObjectDefine.sourceId, sourceField = SourceObjectDefine.id))
    private String extra;
}
