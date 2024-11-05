package org.dromara.mpe.demo.dict;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.autotable.annotation.Table;

/**
 * 字典类
 */
@Data
@Accessors(chain = true)
@Table
public class SysDict {

    private String dictKey;
    private String dictVal;
}
