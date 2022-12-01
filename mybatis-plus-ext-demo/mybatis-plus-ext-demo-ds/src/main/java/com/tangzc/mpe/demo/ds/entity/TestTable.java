package com.tangzc.mpe.demo.ds.entity;

import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.AutoIncrement;
import com.tangzc.mpe.autotable.annotation.Table;

@Table(comment = "表1")
public class TestTable {

    @AutoIncrement
    @ColumnComment("ID")
    private Long id;

    @ColumnComment("用户名")
    private String userName;
}
