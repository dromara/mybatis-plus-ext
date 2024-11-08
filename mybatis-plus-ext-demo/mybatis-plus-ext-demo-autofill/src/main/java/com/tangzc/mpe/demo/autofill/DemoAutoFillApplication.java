package com.tangzc.mpe.demo.autofill;

import com.tangzc.autotable.springboot.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable
@SpringBootApplication
public class DemoAutoFillApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoAutoFillApplication.class, args);
    }

}
