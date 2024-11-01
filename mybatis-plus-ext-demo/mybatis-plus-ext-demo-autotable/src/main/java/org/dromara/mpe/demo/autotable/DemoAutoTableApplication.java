package org.dromara.mpe.demo.autotable;

import com.tangzc.autotable.springboot.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAutoTable
@SpringBootApplication
public class DemoAutoTableApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoAutoTableApplication.class, args);
    }
}
