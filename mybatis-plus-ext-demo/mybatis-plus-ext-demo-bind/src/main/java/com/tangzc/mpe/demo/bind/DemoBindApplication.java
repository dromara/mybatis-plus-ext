package com.tangzc.mpe.demo.bind;

import com.tangzc.autotable.springboot.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable
@SpringBootApplication
public class DemoBindApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoBindApplication.class, args);
    }
}
