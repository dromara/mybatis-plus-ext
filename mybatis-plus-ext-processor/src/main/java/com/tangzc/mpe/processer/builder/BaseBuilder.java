package com.tangzc.mpe.processer.builder;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.magic.util.AnnotationDefaultValueHelper;
import com.tangzc.mpe.processer.config.ConfigurationKey;
import com.tangzc.mpe.processer.config.MybatisPlusExtProcessConfig;
import org.springframework.util.StringUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

public abstract class BaseBuilder {

    protected final Filer filer;
    protected final Messager messager;
    protected final Elements elementUtils;
    protected final MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;

    protected BaseBuilder(Filer filer, Messager messager, Elements elementUtils, MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig) {
        this.filer = filer;
        this.messager = messager;
        this.elementUtils = elementUtils;
        this.mybatisPlusExtProcessConfig = mybatisPlusExtProcessConfig;
    }

    protected String getTargetPackageName(TypeElement classElement, String customPackagePath) {
        // 默认使用entity所在目录
        String entityPackagePath = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
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

    protected String getTargetName(String customName, String entityName, String suffix) {
        String name;

        if (!"".equals(customName)) {
            name = customName;
        } else {
            name = entityName + suffix;
        }
        return name;
    }

    protected void writeToFile(String path, List<String> lines) {
        try {
            JavaFileObject file = this.filer.createSourceFile(path);
            try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                lines.stream().filter(Objects::nonNull).forEach(writer::println);
            }
        } catch (FilerException e) {
            if (e.getMessage().startsWith("Attempt to recreate a file for type ")) {
                return;
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断是否已经被手动创建了
     */
    protected boolean isExist(String packageName, String fileName) {
        if (!fileName.endsWith(".java")) {
            fileName = fileName + ".java";
        }
        FileObject fileObject = null;
        try {
            fileObject = this.filer.getResource(StandardLocation.SOURCE_PATH, packageName, fileName);
        } catch (IOException ignored) {
        }

        if (fileObject != null) {
            log("存在跳过:" + packageName + "." + fileName);
            return true;
        } else {
            log("自动创建:" + packageName + "." + fileName);
            return false;
        }
    }

    protected void log(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    protected void warn(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    protected String getValueOrDefault(String value, ConfigurationKey entityDefineSuffix) {
        if (value.isEmpty()) {
            value = mybatisPlusExtProcessConfig.get(entityDefineSuffix);
        }
        return value;
    }
}
