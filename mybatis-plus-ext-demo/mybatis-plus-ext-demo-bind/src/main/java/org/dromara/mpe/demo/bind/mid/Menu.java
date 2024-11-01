package org.dromara.mpe.demo.bind.mid;

import org.dromara.mpe.annotation.InsertFillTime;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.ColumnId;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoDefine;
import lombok.Data;

/**
 * @author don
 */
@Data
@AutoDefine
@Table(comment = "菜单")
public class Menu {

    @ColumnId(comment = "id")
    private String id;
    @Column(comment = "名称")
    private String name;
    @InsertFillTime
    private String registeredDate;
}
