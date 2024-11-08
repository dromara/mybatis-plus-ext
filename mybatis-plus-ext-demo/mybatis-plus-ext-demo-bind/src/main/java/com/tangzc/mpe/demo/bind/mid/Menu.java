package com.tangzc.mpe.demo.bind.mid;

import lombok.Data;
import lombok.experimental.Accessors;
import com.tangzc.mpe.annotation.InsertFillTime;
import com.tangzc.mpe.autotable.annotation.Table;

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
