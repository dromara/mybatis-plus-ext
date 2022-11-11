package com.tangzc.mpe.demo.datasource;

import com.tangzc.mpe.actable.annotation.Table;
import lombok.Data;

@Data
@Table(comment = "数据源表")
public class SourceObject {

    private String id;
    private String name;
}
