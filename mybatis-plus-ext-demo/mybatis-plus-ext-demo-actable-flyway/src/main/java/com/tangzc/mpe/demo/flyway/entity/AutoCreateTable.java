package com.tangzc.mpe.demo.flyway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import com.tangzc.mpe.autotable.annotation.Table;

@Table(comment = "表1")
public class AutoCreateTable {

    @ColumnId(mode = IdType.AUTO, comment = "ID")
    private Long id;

    @ColumnComment("用户名")
    private String username;
}
