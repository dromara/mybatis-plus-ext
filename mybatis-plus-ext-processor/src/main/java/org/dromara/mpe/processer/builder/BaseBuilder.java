package org.dromara.mpe.processer.builder;

import org.dromara.mpe.processer.config.ConfigurationKey;
import org.dromara.mpe.processer.config.MybatisPlusExtProcessConfig;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseBuilder {

    protected final Filer filer;
    protected final Messager messager;
    protected final Elements elementUtils;
    protected final MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;
    protected final String projectRoot;

    protected BaseBuilder(Filer filer, Messager messager, Elements elementUtils, String projectRoot, MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig) {
        this.filer = filer;
        this.messager = messager;
        this.elementUtils = elementUtils;
        this.projectRoot = projectRoot;
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
        String fullName = packageName + "." + fileName;
        boolean exists = checkByTypeElement(fullName);
        if (!exists) {
            exists = checkBySourcePathResource(fullName);
        }
        if (!exists && projectRoot != null) {
            exists = checkByFileSystem(fullName);
        }

        if (exists) {
            log("存在跳过:" + packageName + "." + fileName);
            return true;
        } else {
            log("自动创建:" + packageName + "." + fileName);
            return false;
        }
    }

    private boolean checkByTypeElement(String fqcn) {
        try {
            TypeElement te = elementUtils.getTypeElement(fqcn);
            return te != null;
        } catch (Exception e) {
            // 理论上不会，但保险起见
            return false;
        }
    }

    private boolean checkBySourcePathResource(String fqcn) {
        String resourcePath = fqcn.replace('.', '/') + ".java";
        try {
            FileObject fo = filer.getResource(StandardLocation.SOURCE_PATH, "", resourcePath);
            // 如果能获取到 FileObject 且能 openInputStream，说明源文件能找到
            try (InputStream is = fo.openInputStream()) {
                // 读取少量字节验证
                if (is.read() != -1) return true;
            } catch (IOException e) {
                // 这里表示即便拿到 FileObject，也可能无法打开（不同实现差异）
                return false;
            }
        } catch (FilerException fe) {
            // FilerException 可能因资源已被创建或其它原因抛出，忽略或记录
        } catch (IOException ioe) {
            // 没找到资源会抛 IOException
            // messager.printMessage(Kind.NOTE, "[ExistenceCheckProcessor] SOURCE_PATH 未找到: " + resourcePath);
        }
        return false;
    }

    private boolean checkByFileSystem(String fqcn) {
        // 直接去 projectRoot/src/main/java 或 src/main/java 之类的位置找
        String relative = fqcn.replace('.', File.separatorChar) + ".java";
        List<Path> candidateRoots = new ArrayList<>();
        candidateRoots.add(Paths.get(projectRoot, "src", "main", "java"));
        candidateRoots.add(Paths.get(projectRoot, "src", "test", "java"));
        candidateRoots.add(Paths.get(projectRoot, "src")); // 兜底
        for (Path root : candidateRoots) {
            Path p = root.resolve(relative);
            if (Files.exists(p)) {
                return true;
            }
        }
        return false;
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
