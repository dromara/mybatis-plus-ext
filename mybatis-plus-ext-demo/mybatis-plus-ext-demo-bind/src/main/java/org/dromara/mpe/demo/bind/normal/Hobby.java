package org.dromara.mpe.demo.bind.normal;

import lombok.experimental.Accessors;
import org.dromara.mpe.autotable.annotation.Table;
import lombok.Data;

@Data
@Table
@Accessors(chain = true)
public class Hobby {

    private String id;
    private String name;

    private String userId;
}
