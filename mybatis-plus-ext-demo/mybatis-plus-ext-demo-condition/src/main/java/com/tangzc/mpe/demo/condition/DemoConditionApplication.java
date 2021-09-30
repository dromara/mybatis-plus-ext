package com.tangzc.mpe.demo.condition;

import com.tangzc.mpe.actable.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable
@SpringBootApplication
public class DemoConditionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoConditionApplication.class, args);
    }

}
