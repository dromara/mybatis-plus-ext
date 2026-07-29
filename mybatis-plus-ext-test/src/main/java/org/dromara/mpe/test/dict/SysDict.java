package org.dromara.mpe.test.dict;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.autotable.annotation.Table;

@Data
@Accessors(chain = true)
@Table
public class SysDict {

    private String dictKey;
    private String dictVal;
}
