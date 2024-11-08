package com.tangzc.mpe.demo.datasource;

import lombok.Data;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.datasource.annotation.Condition;
import com.tangzc.mpe.datasource.annotation.DataSource;

@Data
@Table(comment = "目标表")
public class TargetObject {

    private String id;
    private String name;

    private String sourceId;

    @DataSource(source = SourceObject.class, field = SourceObjectDefine.name, conditions = @Condition(selfField = TargetObjectDefine.sourceId, sourceField = SourceObjectDefine.id))
    private String extra;
}
