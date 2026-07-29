package org.dromara.mpe.test.bind;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.autofill.annotation.InsertFillTime;
import org.dromara.mpe.autotable.annotation.Table;

@Data
@Accessors(chain = true)
@Table
public class BindUser {

    private String id;
    private String name;
    @InsertFillTime
    private Long registeredDate;
}
