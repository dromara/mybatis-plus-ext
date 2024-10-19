package com.tangzc.mpe.demo.condition.user;

import com.tangzc.autotable.annotation.ColumnComment;
import com.tangzc.mpe.annotation.InsertFillData;
import com.tangzc.mpe.annotation.InsertFillTime;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.base.entity.BaseLogicEntity;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AutoRepository
@Accessors(chain = true)
@Table(comment = "用户")
public class User extends BaseLogicEntity<String, LocalDateTime> {

    @ColumnComment("主键")
    private String id;
    @Column(comment = "姓名", length = 300)
    @InsertFillData(UserNameAutoFillHandler.class)
    private String name;
    @Column(comment = "部门姓名", length = 300)
    @InsertFillData(DeptNameAutoFillHandler.class)
    private String deptName;
    @InsertFillTime
    @Column(value = "registered_date_rename", comment = "注册时间")
    private Long registeredDate;
}
