package org.dromara.mpe.demo.autofill;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.autotable.annotation.ColumnComment;
import org.dromara.mpe.autofill.annotation.InsertFillData;
import org.dromara.mpe.autofill.annotation.InsertFillTime;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.Table;

@Data
@Accessors(chain = true)
@Table(comment = "用户")
public class User {

    @ColumnComment("主键")
    private String id;
    @Column(comment = "姓名", length = 300)
    @InsertFillData
    private String otherName;
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
