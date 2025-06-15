package org.dromara.mpe.demo.bind.autoResultMap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.BindEntity;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.processer.annotation.AutoRepository;

import java.util.List;

@FieldNameConstants
@Data
@Table(value = "work_order")
@AutoRepository
public class WorkOrder {

    @TableId(value = "work_order_id", type = IdType.AUTO)
    private Long workOrderId;
    /**
     * 产品合集
     */
    @BindEntity(conditions = @JoinCondition(selfField = WorkOrder.Fields.workOrderId, joinField = WoProduct.Fields.workOrderId))
    @TableField(exist = false)
    private List<WoProduct> woProducts;
}
