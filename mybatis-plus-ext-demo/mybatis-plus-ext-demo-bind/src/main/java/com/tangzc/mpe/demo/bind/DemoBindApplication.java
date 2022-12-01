package com.tangzc.mpe.demo.bind;

import com.tangzc.mpe.autotable.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable(activeProfile = "demo")
@SpringBootApplication
public class DemoBindApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoBindApplication.class, args);
    }
}
