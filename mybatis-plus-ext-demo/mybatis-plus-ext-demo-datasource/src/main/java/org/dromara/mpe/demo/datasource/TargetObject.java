package org.dromara.mpe.demo.datasource;

import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.datasource.annotation.Condition;
import org.dromara.mpe.datasource.annotation.DataSource;
import lombok.Data;

@Data
@Table(comment = "目标表")
public class TargetObject {

    private String id;
    private String name;

    private String sourceId;
    @DataSource(source = SourceObject.class, field = "name", conditions = @Condition(selfField = "sourceId", sourceField = "id"))
    private String extra;
}
