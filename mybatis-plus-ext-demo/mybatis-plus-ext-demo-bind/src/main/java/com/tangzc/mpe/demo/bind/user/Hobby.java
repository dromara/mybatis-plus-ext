package com.tangzc.mpe.demo.bind.user;

import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

@Data
@Table
public class Hobby {

    private String id;
    private String name;

    private String userId;
}
