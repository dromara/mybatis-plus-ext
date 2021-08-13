package com.tangzc.mpe.demo.condition.user;

import com.tangzc.mpe.actable.annotation.Column;
import com.tangzc.mpe.actable.annotation.ColumnComment;
import com.tangzc.mpe.actable.annotation.Table;
import com.tangzc.mpe.annotation.InsertOptionDate;
import com.tangzc.mpe.base.base.BaseLogicEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Table(comment = "用户")
public class User extends BaseLogicEntity<String, LocalDateTime> {

    @ColumnComment("主键")
    private String id;
    @Column(comment = "姓名", length = 300)
    private String name;
    @InsertOptionDate
    @ColumnComment("注册时间")
    private Long registeredDate;
}
