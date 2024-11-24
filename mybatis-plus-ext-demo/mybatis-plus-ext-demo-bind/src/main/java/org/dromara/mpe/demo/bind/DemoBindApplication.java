package org.dromara.mpe.demo.bind;

import org.dromara.autotable.springboot.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable
@SpringBootApplication
public class DemoBindApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoBindApplication.class, args);
    }
}
