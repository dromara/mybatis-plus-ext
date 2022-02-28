package com.tangzc.mpe.demo.autotable.entity;

import com.tangzc.mpe.actable.annotation.ColumnComment;
import com.tangzc.mpe.actable.annotation.Table;

@Table(comment = "表2")
public class TestTable2 {

    @ColumnComment("id")
    private String id;
}
