package com.tangzc.mpe.processer.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.tangzc.mpe.base.repository.BaseRepository;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import com.tangzc.mpe.processer.config.ConfigurationKey;
import com.tangzc.mpe.processer.config.MybatisPlusExtProcessConfig;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class RepositoryBuilder extends BaseBuilder {

    private final Types typeUtils;
    private final Elements elementUtils;
    private final MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;
    private final MapperBuilder mapperBuilder;

    public RepositoryBuilder(Filer filer, Messager messager, Types typeUtils, Elements elementUtils, MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig, MapperBuilder mapperBuilder) {
        super(filer, messager);
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.mybatisPlusExtProcessConfig = mybatisPlusExtProcessConfig;
        this.mapperBuilder = mapperBuilder;
    }

    public void buildRepository(TypeElement element) {
        /* 获取Entity的类名和包名 */
        String entityPackageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
        String entityName = element.getSimpleName().toString();
        AutoRepository autoRepository = element.getAnnotation(AutoRepository.class);

        /* 获取Repository的类名和包名 */
        String suffix = autoRepository.suffix();
        if ("".equals(suffix)) {
            suffix = this.mybatisPlusExtProcessConfig.get(ConfigurationKey.REPOSITORY_SUFFIX);
        }
        String repositoryName = getTargetName(autoRepository.value(), entityName, suffix);
        String customPackageName = autoRepository.packageName();
        if (customPackageName.isEmpty()) {
            customPackageName = mybatisPlusExtProcessConfig.get(ConfigurationKey.REPOSITORY_PACKAGE_NAME);
        }
        String repositoryPackageName = getTargetPackageName(entityPackageName, customPackageName);

        /* 获取mapper的类名和包名 */
        String mapperName;
        String mapperPackageName;
        AutoMapper autoMapper = element.getAnnotation(AutoMapper.class);
        if (autoMapper != null) {
            // @AutoMapper不为空，说明已经通过@AutoMapper直接创建了
            mapperName = getTargetName(autoMapper.value(), entityName, mapperBuilder.getMapperSuffix(autoMapper));
            mapperPackageName = getTargetPackageName(entityPackageName, mapperBuilder.getMapperPackageName(autoMapper));
        } else {
            // 没有独立声明@AutoMapper，需要使用@AutoRepository中的@AutoMapper
            autoMapper = autoRepository.autoMapper();
            // 先构建Mapper
            String fullMapperName = mapperBuilder.buildMapper(element, autoMapper);
            int lastIndexOfPoint = fullMapperName.lastIndexOf(".");
            mapperName = fullMapperName.substring(lastIndexOfPoint + 1);
            mapperPackageName = fullMapperName.substring(0, lastIndexOfPoint);
        }

        /* 生成Repository */
        TypeSpec.Builder builder = TypeSpec.classBuilder(repositoryName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(
                        ClassName.get(BaseRepository.class),
                        ClassName.get(mapperPackageName, mapperName),
                        ClassName.get(entityPackageName, entityName)))
                .addAnnotation(ClassName.get(Repository.class));

        // 添加@DS注解
        if (autoRepository.withDSAnnotation()) {
            addDsAnnotation(element, builder);
        }

        TypeSpec repository = builder.build();

        JavaFile javaFile = JavaFile.builder(repositoryPackageName, repository)
                .build();

        // 持久化到本地
        writeToFile(javaFile);
    }
}
