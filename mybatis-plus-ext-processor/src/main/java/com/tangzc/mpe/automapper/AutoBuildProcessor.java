package com.tangzc.mpe.automapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.tangzc.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author don
 */
@AutoService(Processor.class)
public class AutoBuildProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        mybatisPlusExtProcessConfig = new MybatisPlusExtProcessConfig(this.filer);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annoations, RoundEnvironment env) {

        if (annoations.isEmpty()) {
            return true;
        }
        Set<? extends Element> mapperElements = env.getElementsAnnotatedWith(AutoMapper.class);
        buildMapper(mapperElements);

        Set<? extends Element> repositoryElements = env.getElementsAnnotatedWith(AutoRepository.class);
        buildRepository(repositoryElements);
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(AutoMapper.class.getCanonicalName());
        supportedAnnotations.add(AutoRepository.class.getCanonicalName());
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void buildMapper(Set<? extends Element> elements) {

        Map<String, String> MapperMap = new HashMap<>();

        for (Element element : elements) {

            /* 获取Entity的类名和包名 */
            String entityPackageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String entityName = element.getSimpleName().toString();
            AutoMapper autoMapper = element.getAnnotation(AutoMapper.class);

            buildMapper(entityPackageName, entityName, autoMapper);
        }
    }

    private String buildMapper(String entityPackageName, String entityName, AutoMapper autoMapper) {

        String suffix = getMapperSuffix(autoMapper);
        String mapperName = getTargetName(autoMapper.value(), entityName, suffix);
        String packageName = getMapperPackageName(autoMapper);
        String mapperPackagePath = getTargetPackageName(entityPackageName, packageName);

        ClassName mapperSuperclassName = getMapperSuperclassName(autoMapper);

        /* 生成Mapper */
        TypeSpec mapper = TypeSpec.interfaceBuilder(mapperName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(
                        mapperSuperclassName,
                        ClassName.get(entityPackageName, entityName)))
                .addAnnotation(ClassName.get(Mapper.class))
                .build();
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

    private String getMapperPackageName(AutoMapper autoMapper) {
        String packageName = autoMapper.packageName();
        if (packageName.isEmpty()) {
            packageName = mybatisPlusExtProcessConfig.get(ConfigurationKey.MAPPER_PACKAGE_NAME);
        }
        return packageName;
    }

    private String getMapperSuffix(AutoMapper autoMapper) {
        String suffix = autoMapper.suffix();
        if ("".equals(suffix)) {
            suffix = this.mybatisPlusExtProcessConfig.get(ConfigurationKey.MAPPER_SUFFIX);
        }
        return suffix;
    }

    private void buildRepository(Set<? extends Element> elements) {
        for (Element element : elements) {

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
                mapperName = getTargetName(autoMapper.value(), entityName, getMapperSuffix(autoMapper));
                mapperPackageName = getTargetPackageName(entityPackageName, getMapperPackageName(autoMapper));
            } else {
                // 没有独立声明@AutoMapper，需要使用@AutoRepository中的@AutoMapper
                autoMapper = autoRepository.autoMapper();
                // 先构建Mapper
                String fullMapperName = buildMapper(entityPackageName, entityName, autoMapper);
                int lastIndexOfPoint = fullMapperName.lastIndexOf(".");
                mapperName = fullMapperName.substring(lastIndexOfPoint + 1);
                mapperPackageName = fullMapperName.substring(0, lastIndexOfPoint);
            }

            /* 生成Repository */
            TypeSpec repository = TypeSpec.classBuilder(repositoryName)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ParameterizedTypeName.get(
                            ClassName.get(BaseRepository.class),
                            ClassName.get(mapperPackageName, mapperName),
                            ClassName.get(entityPackageName, entityName)))
                    .addAnnotation(ClassName.get(Repository.class))
                    .build();
            JavaFile javaFile = JavaFile.builder(repositoryPackageName, repository)
                    .build();

            // 持久化到本地
            writeToFile(javaFile);
        }
    }

    private String getTargetPackageName(String entityPackagePath, String customPackagePath) {
        // 默认使用entity所在目录
        String packageName = entityPackagePath;
        if (!"".equals(customPackagePath)) {
            String basePackageName;
            // 相对路径配置
            if (customPackagePath.startsWith(".")) {
                // 先去掉第一个.，赋值当前entity所在目录
                customPackagePath = customPackagePath.substring(1);
                basePackageName = entityPackagePath;
                // 后续如果仍有.，则向上取一层
                while (customPackagePath.startsWith(".")) {
                    // 去除.
                    customPackagePath = customPackagePath.substring(1);
                    // 基本路径去掉最后一层
                    int lastIndexOfPoint = basePackageName.lastIndexOf(".");
                    if (lastIndexOfPoint != -1) {
                        basePackageName = basePackageName.substring(0, lastIndexOfPoint);
                    } else {
                        basePackageName = "";
                    }
                }
                if (!customPackagePath.isEmpty() && !basePackageName.isEmpty()) {
                    customPackagePath = "." + customPackagePath;
                }
                packageName = basePackageName + customPackagePath;
            } else {
                // 绝对路径配置
                packageName = customPackagePath;
            }
        }
        return packageName;
    }

    private String getTargetName(String customName, String entityName, String suffix) {
        String name;

        if (!"".equals(customName)) {
            name = customName;
        } else {
            name = entityName + suffix;
        }
        return name;
    }

    private void writeToFile(JavaFile javaFile) {
        try {
            if (javaFile != null) {
                javaFile.writeTo(this.filer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
