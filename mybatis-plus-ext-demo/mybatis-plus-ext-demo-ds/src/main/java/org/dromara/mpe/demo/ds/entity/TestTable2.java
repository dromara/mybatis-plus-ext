package org.dromara.mpe.demo.ds.entity;

import com.tangzc.autotable.annotation.ColumnComment;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoMapper;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@AutoRepository(withDSAnnotation = true)
@AutoMapper(withDSAnnotation = true)
@Table(comment = "表2", dsName = "test")
@Data
public class TestTable2 {

    @ColumnComment("id")
    private String id;
}
