package com.tangzc.mpe.demo.ds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.Table;

@Table(comment = "表1")
public class TestTable {

    @TableId(type = IdType.AUTO)
    @ColumnComment("ID")
    private Long id;

    @ColumnComment("用户名")
    private String userName;
}
