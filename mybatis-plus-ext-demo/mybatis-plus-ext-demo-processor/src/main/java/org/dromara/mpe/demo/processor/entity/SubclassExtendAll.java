package org.dromara.mpe.demo.processor.entity;

import org.dromara.mpe.processer.annotation.AutoDefine;
import org.dromara.mpe.processer.annotation.AutoMapper;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AutoDefine
@AutoMapper
@AutoRepository
public class SubclassExtendAll extends All {

    private String mark;
}
