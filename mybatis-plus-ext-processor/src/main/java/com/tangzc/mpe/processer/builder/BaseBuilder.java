package com.tangzc.mpe.processer.builder;

import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.io.IOException;

public abstract class BaseBuilder {

    protected final Filer filer;
    protected final Messager messager;

    protected BaseBuilder(Filer filer, Messager messager) {
        this.filer = filer;
        this.messager = messager;
    }

    protected String getTargetPackageName(String entityPackagePath, String customPackagePath) {
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

    protected String getTargetName(String customName, String entityName, String suffix) {
        String name;

        if (!"".equals(customName)) {
            name = customName;
        } else {
            name = entityName + suffix;
        }
        return name;
    }

    protected void writeToFile(JavaFile javaFile) {
        try {
            if (javaFile != null) {
                javaFile.writeTo(this.filer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void log(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
