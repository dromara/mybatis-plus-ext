package com.tangzc.mpe.autotable.constants;

import lombok.Getter;

/**
 * 运行模式
 * @author don
 */
@Getter
public enum RunMode {

    /**
     * 系统不做任何处理
     */
    none,
    /**
     * 系统启动后，会先将所有的表删除掉，然后根据model中配置的结构重新建表，该操作会破坏原有数据
     */
    create,
    /**
     * 系统启动后，会自动判断哪些表是新建的，哪些字段要新增修改，哪些索引/约束要新增删除等，该操作不会删除字段(更改字段名称的情况下，会认为是新增字段)
     */
    update
}
