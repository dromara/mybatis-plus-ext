package com.tangzc.mpe.demo.bind.mid;

import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.processer.annotation.AutoDefine;
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
}
