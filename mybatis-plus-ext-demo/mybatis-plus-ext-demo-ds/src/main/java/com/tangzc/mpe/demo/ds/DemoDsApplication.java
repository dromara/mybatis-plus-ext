package com.tangzc.mpe.demo.ds;

import com.tangzc.autotable.springboot.EnableAutoTable;
import com.tangzc.mpe.demo.ds.repository.TestTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@EnableAutoTable
@SpringBootApplication
public class DemoDsApplication {

    @Autowired
    private TestTableRepository testTableRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoDsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void test() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testTableRepository.list();
    }
}
