package org.dromara.mpe.processer;

import com.google.auto.service.AutoService;
import org.dromara.mpe.magic.util.AnnotationDefaultValueHelper;
import org.dromara.mpe.processer.annotation.AutoDefine;
import org.dromara.mpe.processer.annotation.AutoMapper;
import org.dromara.mpe.processer.annotation.AutoRepository;
import org.dromara.mpe.processer.builder.DefineBuilder;
import org.dromara.mpe.processer.builder.MapperBuilder;
import org.dromara.mpe.processer.builder.RepositoryBuilder;
import org.dromara.mpe.processer.config.ConfigurationKey;
import org.dromara.mpe.processer.config.MybatisPlusExtProcessConfig;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author don
 */
@AutoService(Processor.class)
@SupportedOptions({"projectRoot", "check.targets"}) // 可传 projectRoot、和要检测的 targets 列表（逗号分隔）
public class AutoBuildProcessor extends AbstractProcessor {

    private DefineBuilder defineBuilder;
    private MapperBuilder mapperBuilder;
    private RepositoryBuilder repositoryBuilder;
    private MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;
    private Class<? extends Annotation> globalAnnotationClass;
    private String projectRoot; // 可选的 fallback 根目录

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Types typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();
        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        mybatisPlusExtProcessConfig = MybatisPlusExtProcessConfig.getInstance(filer);

        Map<String, String> options = processingEnv.getOptions();
        this.projectRoot = options.get("projectRoot");


        defineBuilder = new DefineBuilder(filer, messager, typeUtils, elementUtils, projectRoot, mybatisPlusExtProcessConfig);
        mapperBuilder = new MapperBuilder(filer, messager, typeUtils, elementUtils, projectRoot, mybatisPlusExtProcessConfig);
        repositoryBuilder = new RepositoryBuilder(filer, messager, typeUtils, elementUtils, projectRoot, mybatisPlusExtProcessConfig, mapperBuilder);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annoations, RoundEnvironment env) {

        if (annoations.isEmpty()) {
            return true;
        }

        // Class<? extends Annotation> globalAnnotationClass = getGlobalType();

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
        boolean isGlobalMode = "true".equalsIgnoreCase(globalEnable) || "on".equalsIgnoreCase(globalEnable);
        if (isGlobalMode) {
            String globalAnnotationName = mybatisPlusExtProcessConfig.get(ConfigurationKey.GLOBAL_ENABLE_ANNOTATION);
            if (globalAnnotationName.matches("^([a-zA-Z$][a-zA-Z\\d_$]*\\.)*([a-zA-Z$][a-zA-Z\\d_$]*)$")) {
                supportedAnnotations.add(globalAnnotationName);
            } else {
                throw new RuntimeException("【代码生成】配置的全局注解类名格式不正确：\"" + globalAnnotationName + "\"");
            }
            try {
                globalAnnotationClass = (Class<? extends Annotation>) Class.forName(globalAnnotationName);
            } catch (Exception e) {
                throw new RuntimeException("【代码生成】未找到全局注解", e);
            }
        }

        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
