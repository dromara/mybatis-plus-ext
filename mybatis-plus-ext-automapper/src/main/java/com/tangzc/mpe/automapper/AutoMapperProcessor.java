package com.tangzc.mpe.automapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.ibatis.annotations.Mapper;

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
import java.util.Collections;
import java.util.Set;

/**
 * @author don
 */
@AutoService(Processor.class)
public class AutoMapperProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annoations, RoundEnvironment env) {

        if (annoations.isEmpty()) {
            return true;
        }
        for (Element element : env.getElementsAnnotatedWith(AutoMapper.class)) {

            /* 获取Entity的类名和包名 */
            String entityPackageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String entityName = element.getSimpleName().toString();

            /* 获取mapper的类名和包名 */
            AutoMapper autoMapper = element.getAnnotation(AutoMapper.class);
            String mapperName;
            if (!"".equals(autoMapper.value())) {
                mapperName = autoMapper.value();
            } else {
                mapperName = entityName + autoMapper.suffix();
            }
            String mapperPackageName = entityPackageName;
            if (!"".equals(autoMapper.packageName())) {
                mapperPackageName = autoMapper.packageName();
            }

            /* 生成Mapper */
            TypeSpec mapper = TypeSpec.interfaceBuilder(mapperName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ParameterizedTypeName.get(
                            ClassName.get(BaseMapper.class),
                            ClassName.get(entityPackageName, entityName)))
                    .addAnnotation(ClassName.get(Mapper.class))
                    .build();
            JavaFile javaFile = JavaFile.builder(mapperPackageName, mapper)
                    .build();

            // 持久化到本地
            try {
                javaFile.writeTo(this.filer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoMapper.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
