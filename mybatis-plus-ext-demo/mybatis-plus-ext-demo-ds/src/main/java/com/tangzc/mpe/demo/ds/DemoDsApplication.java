package com.tangzc.mpe.demo.ds;

import com.tangzc.mpe.actable.EnableAutoTable;
import com.tangzc.mpe.demo.ds.repository.TestTableRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;

@EnableAutoTable
@SpringBootApplication
public class DemoDsApplication {

    @Resource
    private TestTableRepository testTableRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoDsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void test() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("======================");
        System.out.println("======================");
        testTableRepository.list();
    }
}
