package org.dromara.mpe.demo.bind.user;

import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoDefine;
import lombok.Data;

@Data
@Table
@AutoDefine
public class Hobby {

    private String id;
    private String name;

    private String userId;
}
