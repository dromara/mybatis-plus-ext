package com.tangzc.mpe.demo.autotable.mysql;

import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

/**
 * @author don
 */
@Data
@Table
public class MysqlTable2 {

    private String id;

    private String username;

    private Integer age;

    private String phone;
}
