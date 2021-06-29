package com.tangzc.mpe.core.base;

import com.tangzc.mpe.core.annotation.InsertOptionDate;
import com.tangzc.mpe.core.annotation.InsertOptionUser;
import com.tangzc.mpe.core.annotation.InsertUpdateOptionDate;
import com.tangzc.mpe.core.annotation.InsertUpdateOptionUser;
import com.tangzc.mpe.core.handler.IOptionByAutoFillHandler;
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
    private ID_TYPE createBy;
    @InsertUpdateOptionUser(IOptionByAutoFillHandler.class)
    private ID_TYPE updateBy;
    @InsertOptionDate
    private TIME_TYPE createTime;
    @InsertUpdateOptionDate
    private TIME_TYPE updateTime;
}
