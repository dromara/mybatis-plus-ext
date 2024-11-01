package org.dromara.mpe.demo.processor.entity;

import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@AutoRepository
@Data
public class OnlyRepository {

    private String id;
    private String name;
    private int age;
}
