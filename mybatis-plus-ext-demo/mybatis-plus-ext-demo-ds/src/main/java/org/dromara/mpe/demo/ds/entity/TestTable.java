package org.dromara.mpe.demo.ds.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.autotable.annotation.ColumnComment;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.BindEntity;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.processer.annotation.AutoRepository;
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
