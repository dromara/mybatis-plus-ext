package com.tangzc.mpe.autotable.strategy.sqlite.data.dbdata;

import lombok.Data;

/**
 * sqlite记录表和索引元信息的表
 * @author don
 */
@Data
public class SqliteMaster {

    private String type;
    private String name;
    private String tblName;
    private Integer rootpage;
    private String sql;
}
