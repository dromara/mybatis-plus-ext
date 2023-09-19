package com.tangzc.mpe.demo.datasource;

import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.datasource.annotation.Condition;
import com.tangzc.mpe.datasource.annotation.DataSource;
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
