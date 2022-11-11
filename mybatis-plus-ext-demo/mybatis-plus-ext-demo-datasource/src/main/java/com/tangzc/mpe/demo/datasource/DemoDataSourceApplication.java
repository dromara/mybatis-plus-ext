package com.tangzc.mpe.demo.datasource;

import com.tangzc.mpe.actable.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable(activeProfile = "demo")
@SpringBootApplication
public class DemoDataSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDataSourceApplication.class, args);
    }
}
