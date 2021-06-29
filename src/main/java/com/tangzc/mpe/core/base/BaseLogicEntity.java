package com.tangzc.mpe.core.base;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.tangzc.mpe.core.annotation.DefaultValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author don
 */
@Getter
@Setter
public class BaseLogicEntity<ID_TYPE extends Serializable, TIME_TYPE> extends BaseEntity<ID_TYPE, TIME_TYPE> {

    @TableLogic
    @DefaultValue("0")
    private Integer deleted;
}