package com.tangzc.mpe.demo.ds.entity;

import com.tangzc.mpe.actable.annotation.ColumnComment;
import com.tangzc.mpe.actable.annotation.Table;

@Table(comment = "表1")
public class TestTable {

    @ColumnComment("id")
    private String id;
}
