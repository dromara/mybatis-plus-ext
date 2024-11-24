package org.dromara.mpe.demo.autofill;

import org.dromara.autotable.springboot.EnableAutoTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoTable
@SpringBootApplication
public class DemoAutoFillApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoAutoFillApplication.class, args);
    }

}
