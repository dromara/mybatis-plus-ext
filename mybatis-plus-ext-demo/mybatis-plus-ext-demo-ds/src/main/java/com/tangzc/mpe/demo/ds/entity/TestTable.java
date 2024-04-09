package com.tangzc.mpe.demo.ds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import com.tangzc.mpe.datasource.annotation.Condition;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@AutoRepository
@Table(comment = "表1")
@Data
public class TestTable {

    @TableId(type = IdType.AUTO)
    @ColumnComment("ID")
    private Long id;

    @ColumnComment("用户名")
    private String userName;

    @BindEntity(conditions = @JoinCondition(selfField = "id"))
    private TestTable2 testTable2;
}
