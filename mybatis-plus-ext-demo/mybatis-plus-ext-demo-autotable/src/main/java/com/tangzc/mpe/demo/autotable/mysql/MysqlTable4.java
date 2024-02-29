package com.tangzc.mpe.demo.autotable.mysql;

import com.tangzc.autotable.annotation.Ignore;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

/**
 * @author don
 */
@Data
@Ignore
@Table
public class MysqlTable4 {

    @Column(comment = "测试float的数据长度变化问题", length = 19, decimalLength = 6, defaultValue = "0")
    private Float number;
}
