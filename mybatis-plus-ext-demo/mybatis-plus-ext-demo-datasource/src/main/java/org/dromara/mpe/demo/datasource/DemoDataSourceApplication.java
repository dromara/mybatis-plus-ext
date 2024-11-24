package org.dromara.mpe.demo.datasource;

import org.dromara.autotable.springboot.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable
@SpringBootApplication
public class DemoDataSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDataSourceApplication.class, args);
    }
}
