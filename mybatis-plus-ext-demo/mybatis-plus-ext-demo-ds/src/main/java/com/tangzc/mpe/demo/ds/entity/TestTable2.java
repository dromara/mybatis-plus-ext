package com.tangzc.mpe.demo.ds.entity;

import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.Table;

@Table(comment = "表2", dsName = "test")
public class TestTable2 {

    @ColumnComment("id")
    private String id;
}
