package com.tangzc.mpe.core.base;

import com.tangzc.mpe.annotation.actable.ColumnComment;
import com.tangzc.mpe.annotation.entity.InsertOptionDate;
import com.tangzc.mpe.annotation.entity.InsertOptionUser;
import com.tangzc.mpe.annotation.entity.InsertUpdateOptionDate;
import com.tangzc.mpe.annotation.entity.InsertUpdateOptionUser;
import com.tangzc.mpe.annotation.handler.IOptionByAutoFillHandler;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author don
 */
@Getter
@Setter
public class BaseEntity<ID_TYPE extends Serializable, TIME_TYPE> {

    @InsertOptionUser(IOptionByAutoFillHandler.class)
    @ColumnComment("创建人")
    protected ID_TYPE createBy;
    @InsertUpdateOptionUser(IOptionByAutoFillHandler.class)
    @ColumnComment("最后更新人")
    protected ID_TYPE updateBy;
    @InsertOptionDate
    @ColumnComment("创建时间")
    protected TIME_TYPE createTime;
    @InsertUpdateOptionDate
    @ColumnComment("最后更新时间")
    protected TIME_TYPE updateTime;
}
