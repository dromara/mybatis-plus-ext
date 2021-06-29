package com.tangzc.mpe.core.base;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.tangzc.mpe.core.annotation.DefaultValue;
import lombok.Getter;
import lombok.Setter;

/**
 * @author don
 */
@Getter
@Setter
public class LogicEntity {

    @TableLogic
    @DefaultValue("0")
    private Integer deleted;
}