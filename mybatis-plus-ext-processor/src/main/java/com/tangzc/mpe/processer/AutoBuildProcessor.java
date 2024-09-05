package com.tangzc.mpe.processer;

import com.google.auto.service.AutoService;
import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import com.tangzc.mpe.processer.builder.DefineBuilder;
import com.tangzc.mpe.processer.builder.MapperBuilder;
import com.tangzc.mpe.processer.builder.RepositoryBuilder;
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
import java.util.HashSet;
import java.util.Set;

/**
 * @author don
 */
@AutoService(Processor.class)
public class AutoBuildProcessor extends AbstractProcessor {

    private DefineBuilder defineBuilder;
    private MapperBuilder mapperBuilder;
    private RepositoryBuilder repositoryBuilder;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Types typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();
        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig = new MybatisPlusExtProcessConfig(filer);

        defineBuilder = new DefineBuilder(filer, messager, typeUtils, elementUtils, mybatisPlusExtProcessConfig);
        mapperBuilder = new MapperBuilder(filer, messager, typeUtils, elementUtils, mybatisPlusExtProcessConfig);
        repositoryBuilder = new RepositoryBuilder(filer, messager, typeUtils, elementUtils, mybatisPlusExtProcessConfig, mapperBuilder);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annoations, RoundEnvironment env) {

        if (annoations.isEmpty()) {
            return true;
        }

        for (Element element : env.getElementsAnnotatedWith(AutoDefine.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                defineBuilder.buildDefine(classElement);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(AutoMapper.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                mapperBuilder.buildMapper(classElement);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(AutoRepository.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                repositoryBuilder.buildRepository(classElement);
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(AutoDefine.class.getCanonicalName());
        supportedAnnotations.add(AutoMapper.class.getCanonicalName());
        supportedAnnotations.add(AutoRepository.class.getCanonicalName());
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
