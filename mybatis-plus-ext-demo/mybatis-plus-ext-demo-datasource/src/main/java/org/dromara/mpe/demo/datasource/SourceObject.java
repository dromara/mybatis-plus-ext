package org.dromara.mpe.demo.datasource;

import org.dromara.mpe.autotable.annotation.Table;
import lombok.Data;

@Data
@Table(comment = "数据源表")
public class SourceObject {

    private String id;
    private String name;
}
