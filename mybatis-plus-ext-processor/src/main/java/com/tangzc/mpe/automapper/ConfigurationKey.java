/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.tangzc.mpe.automapper;

import lombok.Getter;

/**
 * 配置键值。
 */
@Getter
public enum ConfigurationKey {

    /**
     * 生成的Mapper的后缀。
     */
    MAPPER_SUFFIX("processor.mapperSuffix", "Mapper"),

    /**
     * <p>mapper生成后的路径，支持相对路径的写法
     * <p>比如：..mapper，..表示在上一级目录，即实体所在目录同级的mapper目录下
     * <p>比如：. 表示在实体所在的目录下
     */
    MAPPER_PACKAGE_NAME("processor.mapperPackageName", "."),

    /**
     * <p>指定的Mapper的父类，通常用于自定义Mapper的场景
     * <p>要求：
     * <p>1、值需要是类的全路径
     * <p>2、自定义的父类Mapper必须继承自com.baomidou.mybatisplus.core.mapper.BaseMapper
     * <p>3、自定义的父类Mapper必须保留泛型T
     */
    MAPPER_SUPERCLASS_NAME("processor.mapperSuperclassName", ""),

    /**
     * 生成的Repository的后缀。
     */
    REPOSITORY_SUFFIX("processor.repositorySuffix", "Repository"),

    /**
     * <p>Repository生成后的路径，支持相对路径的写法
     * <p>比如：..repository，..表示在上一级目录，即实体所在目录同级的repository目录下
     * <p>比如：. 表示在实体所在的目录下
     */
    REPOSITORY_PACKAGE_NAME("processor.repositoryPackageName", ".");

    /**
     * -- GETTER --
     *  获取配置键。
     *
     * @return 键
     */
    private final String configKey;
    /**
     * -- GETTER --
     *  获取配置默认值。
     *
     * @return 默认值
     */
    private final String defaultValue;

    ConfigurationKey(String configKey, String defaultValue) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

}
