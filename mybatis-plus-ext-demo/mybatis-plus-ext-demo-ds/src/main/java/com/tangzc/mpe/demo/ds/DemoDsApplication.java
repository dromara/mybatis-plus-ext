package com.tangzc.mpe.demo.ds;

import com.tangzc.mpe.actable.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable
@SpringBootApplication
public class DemoDsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDsApplication.class, args);
    }

}
