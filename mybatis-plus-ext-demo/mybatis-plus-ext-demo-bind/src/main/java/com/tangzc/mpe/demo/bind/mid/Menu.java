package com.tangzc.mpe.demo.bind.mid;

import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

/**
 * @author don
 */
@Data
@Table(comment = "菜单")
public class Menu {

    @Column(comment = "id")
    private String id;
    @Column(comment = "名称")
    private String name;
}
