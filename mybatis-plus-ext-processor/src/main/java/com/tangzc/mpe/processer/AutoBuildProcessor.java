package com.tangzc.mpe.processer;

import com.google.auto.service.AutoService;
import com.tangzc.mpe.magic.util.AnnotationDefaultValueHelper;
import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import com.tangzc.mpe.processer.builder.DefineBuilder;
import com.tangzc.mpe.processer.builder.MapperBuilder;
import com.tangzc.mpe.processer.builder.RepositoryBuilder;
import com.tangzc.mpe.processer.config.ConfigurationKey;
import com.tangzc.mpe.processer.config.MybatisPlusExtProcessConfig;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author don
 */
@AutoService(Processor.class)
public class AutoBuildProcessor extends AbstractProcessor {

    private DefineBuilder defineBuilder;
    private MapperBuilder mapperBuilder;
    private Elements elementUtils;
    private RepositoryBuilder repositoryBuilder;
    private MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;
    private boolean isGlobalMode;
    private String globalActiveAnnotation;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Types typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        mybatisPlusExtProcessConfig = MybatisPlusExtProcessConfig.getInstance(filer);

        defineBuilder = new DefineBuilder(filer, messager, typeUtils, elementUtils, mybatisPlusExtProcessConfig);
        mapperBuilder = new MapperBuilder(filer, messager, typeUtils, elementUtils, mybatisPlusExtProcessConfig);
        repositoryBuilder = new RepositoryBuilder(filer, messager, typeUtils, elementUtils, mybatisPlusExtProcessConfig, mapperBuilder);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annoations, RoundEnvironment env) {

        if (annoations.isEmpty()) {
            return true;
        }

        Class<? extends Annotation> globalAnnotationClass = getGlobalType(annoations);

        Set<? extends Element> rootElements = env.getRootElements();
        for (Element element : rootElements) {
            if (element.getKind() != ElementKind.CLASS) {
                continue;
            }

            boolean withGlobalAnnotation = false;
            if (globalAnnotationClass != null) {
                withGlobalAnnotation = element.getAnnotation(globalAnnotationClass) != null;
            }
            // System.out.println("全局模式：" + withGlobalAnnotation);

            /* 创建Define */
            AutoDefine autoDefine = findAnnotation(element, AutoDefine.class, withGlobalAnnotation);
            if (autoDefine != null) {
                // System.out.println(element.getSimpleName() + "创建Define:" + autoDefine);
                defineBuilder.buildDefine((TypeElement) element, autoDefine);
            }

            /* 创建Mapper */
            String fullMapperName = null;
            AutoMapper autoMapper = findAnnotation(element, AutoMapper.class, withGlobalAnnotation);
            if (autoMapper != null) {
                // System.out.println(element.getSimpleName() + "创建Mapper:" + autoMapper);
                fullMapperName = mapperBuilder.buildMapper((TypeElement) element, autoMapper);
            }

            /* 创建Repository */
            AutoRepository autoRepository = findAnnotation(element, AutoRepository.class, withGlobalAnnotation);
            if (autoRepository != null) {
                // System.out.println(element.getSimpleName() + "创建Repository" + autoRepository);
                // 上一步没有创建mapper的话，这一步必须创建，因为Repository需要Mapper
                if (fullMapperName == null) {
                    // System.out.println(element.getSimpleName() + "创建Mapper2");
                    autoMapper = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(AutoMapper.class, null);
                    fullMapperName = mapperBuilder.buildMapper((TypeElement) element, autoMapper);
                }
                repositoryBuilder.buildRepository((TypeElement) element, autoRepository, fullMapperName);
            }
        }

        return true;
    }

    private Class<? extends Annotation> getGlobalType(Set<? extends TypeElement> annoations) {

        if (!isGlobalMode) {
            return null;
        }

        TypeElement globalType = annoations.stream()
                .filter(type -> type.getQualifiedName().toString().equals(globalActiveAnnotation))
                .findFirst()
                .orElse(null);
        if (globalType == null) {
            throw new RuntimeException("未找到全局配置注解");
        }

        if (globalType.getKind() != ElementKind.ANNOTATION_TYPE) {
            throw new RuntimeException("全局配置注解必须为注解类型");
        }

        // 获取全限定类名
        String className = elementUtils.getBinaryName(globalType).toString();
        // 使用反射根据类名加载Class对象
        try {
            return (Class<? extends Annotation>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <ANNO extends Annotation> ANNO findAnnotation(Element typeElement, Class<ANNO> annotationClass, boolean isFindDefault) {
        ANNO annotation = typeElement.getAnnotation(annotationClass);
        if (annotation == null && isFindDefault) {
            annotation = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(annotationClass, null);
        }
        return annotation;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(AutoDefine.class.getCanonicalName());
        supportedAnnotations.add(AutoMapper.class.getCanonicalName());
        supportedAnnotations.add(AutoRepository.class.getCanonicalName());

        String globalEnable = mybatisPlusExtProcessConfig.get(ConfigurationKey.GLOBAL_ENABLE);
        isGlobalMode = "true".equalsIgnoreCase(globalEnable) || "on".equalsIgnoreCase(globalEnable);
        if (isGlobalMode) {
            globalActiveAnnotation = mybatisPlusExtProcessConfig.get(ConfigurationKey.GLOBAL_ENABLE_ANNOTATION);
            supportedAnnotations.add(globalActiveAnnotation);
        }

        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
