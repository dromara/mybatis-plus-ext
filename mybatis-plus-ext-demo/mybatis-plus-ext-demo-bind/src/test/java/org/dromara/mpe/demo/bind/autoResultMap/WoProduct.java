package org.dromara.mpe.demo.bind.autoResultMap;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.BindField;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.processer.annotation.AutoRepository;

@Data
@FieldNameConstants
@Table(value = "wo_product")
@AutoRepository
public class WoProduct {

    private Long id;

    @Column(comment = "工单ID")
    @TableField(insertStrategy = FieldStrategy.ALWAYS,updateStrategy = FieldStrategy.ALWAYS)
    private Long workOrderId;

    @Column(comment = "产品ID")
    @TableField(insertStrategy = FieldStrategy.ALWAYS,updateStrategy = FieldStrategy.ALWAYS)
    private Long productId;

    /**
     * 产品名称
     */
    @BindField(
            entity = Product.class,
            field = Product.Fields.productName,
            conditions = @JoinCondition(selfField = WoProduct.Fields.productId, joinField = Product.Fields.productId))
    @TableField(exist = false)
    private String productName;
}
