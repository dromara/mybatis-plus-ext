package com.tangzc.mpe.base.entity;

import com.tangzc.autotable.annotation.ColumnComment;
import com.tangzc.mpe.annotation.InsertFillData;
import com.tangzc.mpe.annotation.InsertFillTime;
import com.tangzc.mpe.annotation.InsertUpdateFillTime;
import com.tangzc.mpe.annotation.handler.IAutoFillHandlerForBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author don
 */
@Getter
@Setter
public class BaseEntity<ID_TYPE extends Serializable, TIME_TYPE> {

    @InsertFillData(IAutoFillHandlerForBaseEntity.class)
    @ColumnComment("创建人")
    protected ID_TYPE createBy;
    @InsertFillData(IAutoFillHandlerForBaseEntity.class)
    @ColumnComment("最后更新人")
    protected ID_TYPE updateBy;
    @InsertFillTime
    @ColumnComment("创建时间")
    protected TIME_TYPE createTime;
    @InsertUpdateFillTime
    @ColumnComment("最后更新时间")
    protected TIME_TYPE updateTime;
}
