package com.tangzc.mpe.demo.condition;

import com.tangzc.mpe.actable.EnableAutoTable;
import com.tangzc.mpe.demo.common.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@EnableAutoTable
@SpringBootApplication
@Import(Config.class)
public class DemoConditionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoConditionApplication.class, args);
    }

}
