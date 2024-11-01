package org.dromara.mpe.demo.dict;

import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Table
@AutoRepository
public class SysDict {

    private String dictKey;
    private String dictVal;
}
