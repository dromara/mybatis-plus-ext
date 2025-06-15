package org.dromara.mpe.demo.bind.autoResultMap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoRepository;

import java.util.ArrayList;
import java.util.List;

@Table(value = "product", comment = "产品表")
@TableName(autoResultMap = true)
@Data
@FieldNameConstants
@AutoRepository
@NoArgsConstructor // 添加无参构造器
public class Product {
    /**
     * 产品ID
     */
    @TableId(value = "product_id", type = IdType.AUTO)
    @Column(comment = "产品ID")
    private Long productId;

    /**
     * 产品名称
     */
    @Column(comment = "产品名称")
    private String productName;

    /**
     * 附件
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> fileNames = new ArrayList<>();
}
