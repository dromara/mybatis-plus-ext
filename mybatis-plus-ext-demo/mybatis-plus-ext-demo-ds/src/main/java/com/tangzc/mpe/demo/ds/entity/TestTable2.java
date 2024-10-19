package com.tangzc.mpe.demo.ds.entity;

import com.tangzc.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@AutoRepository(withDSAnnotation = true)
@AutoMapper(withDSAnnotation = true)
@Table(comment = "表2", dsName = "test")
@Data
public class TestTable2 {

    @ColumnComment("id")
    private String id;
}
