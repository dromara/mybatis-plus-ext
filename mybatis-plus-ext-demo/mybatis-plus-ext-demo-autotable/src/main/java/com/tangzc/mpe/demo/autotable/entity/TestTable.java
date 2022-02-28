package com.tangzc.mpe.demo.autotable.entity;

import com.tangzc.mpe.actable.annotation.ColumnComment;
import com.tangzc.mpe.actable.annotation.IsAutoIncrement;
import com.tangzc.mpe.actable.annotation.Table;

@Table(comment = "表1")
public class TestTable {

    @IsAutoIncrement
    @ColumnComment("ID")
    private Long id;

    @ColumnComment("用户名")
    private String userName;
}
