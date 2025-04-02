package org.dromara.mpe.demo.processor.entity;

import lombok.Data;
import org.dromara.mpe.processer.annotation.AutoRepository;

@AutoRepository(superclassName = "org.dromara.mpe.demo.processor.repository.CustomSuperRepository")
@Data
public class OnlyRepositoryCustomSupper {

    private String id;
    private String name;
    private int age;
}
