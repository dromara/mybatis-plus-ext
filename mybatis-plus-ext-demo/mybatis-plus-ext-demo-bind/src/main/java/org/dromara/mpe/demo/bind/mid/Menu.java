package org.dromara.mpe.demo.bind.mid;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.annotation.InsertFillTime;
import org.dromara.mpe.autotable.annotation.Table;

/**
 * @author don
 */
@Data
@Accessors(chain = true)
@Table(comment = "菜单")
public class Menu {

    private String id;
    private String name;
    @InsertFillTime
    private String createTime;
}
