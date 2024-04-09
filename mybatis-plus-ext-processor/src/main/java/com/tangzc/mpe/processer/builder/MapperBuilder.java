package com.tangzc.mpe.processer.builder;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import com.tangzc.mpe.processer.config.ConfigurationKey;
import com.tangzc.mpe.processer.config.MybatisPlusExtProcessConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class MapperBuilder extends BaseBuilder {

    private final Types typeUtils;
    private final Elements elementUtils;
    private final MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;

    public MapperBuilder(Filer filer, Messager messager, Types typeUtils, Elements elementUtils, MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig) {
        super(filer, messager);
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.mybatisPlusExtProcessConfig = mybatisPlusExtProcessConfig;
    }

    public void buildMapper(TypeElement classElement) {
        /* 获取Entity的类名和包名 */
        AutoMapper autoMapper = classElement.getAnnotation(AutoMapper.class);

        buildMapper(classElement, autoMapper);
    }


    public String buildMapper(TypeElement element, AutoMapper autoMapper) {

        String entityPackageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
        String entityName = element.getSimpleName().toString();

        String suffix = getMapperSuffix(autoMapper);
        String mapperName = getTargetName(autoMapper.value(), entityName, suffix);
        String packageName = getMapperPackageName(autoMapper);
        String mapperPackagePath = getTargetPackageName(entityPackageName, packageName);

        ClassName mapperSuperclassName = getMapperSuperclassName(autoMapper);

        /* 生成Mapper */
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(mapperName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(
                        mapperSuperclassName,
                        ClassName.get(entityPackageName, entityName)))
                .addAnnotation(ClassName.get(Mapper.class));

        // 添加@DS注解
        if (autoMapper.withDSAnnotation()) {
            addDsAnnotation(element, builder);
        }

        TypeSpec mapper = builder.build();
        JavaFile javaFile = JavaFile.builder(mapperPackagePath, mapper)
                .build();

        // 持久化到本地
        writeToFile(javaFile);

        return mapperPackagePath + "." + mapperName;
    }

    private ClassName getMapperSuperclassName(AutoMapper autoMapper) {

        ClassName mapperSuperclassName = ClassName.get(BaseMapper.class);
        String baseMapperClassName = autoMapper.superclassName();
        if (baseMapperClassName.isEmpty()) {
            baseMapperClassName = mybatisPlusExtProcessConfig.get(ConfigurationKey.MAPPER_SUPERCLASS_NAME);
        }
        if (!baseMapperClassName.isEmpty()) {
            int lastIndexOf = baseMapperClassName.lastIndexOf(".");
            String baseMapperPackageName = baseMapperClassName.substring(0, lastIndexOf);
            String baseMapperName = baseMapperClassName.substring(lastIndexOf + 1);
            mapperSuperclassName = ClassName.get(baseMapperPackageName, baseMapperName);
        }
        return mapperSuperclassName;
    }

    public String getMapperPackageName(AutoMapper autoMapper) {
        String packageName = autoMapper.packageName();
        if (packageName.isEmpty()) {
            packageName = mybatisPlusExtProcessConfig.get(ConfigurationKey.MAPPER_PACKAGE_NAME);
        }
        return packageName;
    }

    public String getMapperSuffix(AutoMapper autoMapper) {
        String suffix = autoMapper.suffix();
        if ("".equals(suffix)) {
            suffix = this.mybatisPlusExtProcessConfig.get(ConfigurationKey.MAPPER_SUFFIX);
        }
        return suffix;
    }
}
