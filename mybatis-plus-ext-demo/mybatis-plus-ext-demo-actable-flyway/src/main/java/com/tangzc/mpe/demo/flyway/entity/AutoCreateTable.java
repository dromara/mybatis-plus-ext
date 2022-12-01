package com.tangzc.mpe.demo.flyway.entity;

import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.AutoIncrement;
import com.tangzc.mpe.autotable.annotation.Table;

@Table(comment = "表1")
public class AutoCreateTable {

    @AutoIncrement
    @ColumnComment("ID")
    private Long id;

    @ColumnComment("用户名")
    private String username;
}
