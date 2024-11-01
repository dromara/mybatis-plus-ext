package org.dromara.mpe.demo.processor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.dromara.mpe.processer.annotation.AutoDefine;
import lombok.Data;

@Data
@AutoDefine
@TableName("only_define")
public class OnlyDefine {

    private String id;
    private String name;
    private int age;
}
