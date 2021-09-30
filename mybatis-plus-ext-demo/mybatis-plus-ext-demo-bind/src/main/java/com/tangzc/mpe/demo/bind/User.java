package com.tangzc.mpe.demo.bind;

import com.tangzc.mpe.annotation.InsertOptionDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

    private String id;
    private String name;
    @InsertOptionDate
    private Long registeredDate;
}
