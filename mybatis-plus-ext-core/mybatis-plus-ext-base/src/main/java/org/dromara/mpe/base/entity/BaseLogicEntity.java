package org.dromara.mpe.base.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import org.dromara.autotable.annotation.ColumnComment;
import org.dromara.autotable.annotation.ColumnDefault;
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
    @ColumnDefault("0")
    @ColumnComment("逻辑删除标志")
    protected Integer deleted;
}
