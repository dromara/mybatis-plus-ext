package com.tangzc.mpe.processer.builder;

import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.config.ConfigurationKey;
import com.tangzc.mpe.processer.config.MybatisPlusExtProcessConfig;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.PrintWriter;
import java.util.Set;
import java.util.stream.Collectors;

public class DefineBuilder extends BaseBuilder {

    private final Types typeUtils;
    private final Elements elementUtils;
    private final MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;

    public DefineBuilder(Filer filer, Messager messager, Types typeUtils, Elements elementUtils, MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig) {
        super(filer, messager);
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.mybatisPlusExtProcessConfig = mybatisPlusExtProcessConfig;
    }

    public void buildDefine(TypeElement classElement) {

        // 获取当前类中的所有字段
        Set<String> fields = classElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD && !e.getModifiers().contains(Modifier.STATIC))
                .map(e -> e.getSimpleName().toString())
                .collect(Collectors.toSet());
        // 检索父类中的可用字段
        TypeMirror superClassMirror = classElement.getSuperclass();
        while (!superClassMirror.toString().equals(Object.class.getName())) {
            TypeElement superClassElement = (TypeElement) typeUtils.asElement(superClassMirror);
            fields.addAll(
                    superClassElement.getEnclosedElements().stream()
                            .filter(e -> {
                                Set<Modifier> modifiers = e.getModifiers();
                                return e.getKind() == ElementKind.FIELD &&
                                        modifiers.contains(Modifier.PROTECTED) &&
                                        !modifiers.contains(Modifier.STATIC);
                            })
                            .map(e -> e.getSimpleName().toString())
                            .collect(Collectors.toSet())
            );
            superClassMirror = superClassElement.getSuperclass();
        }

        AutoDefine autoDefine = classElement.getAnnotation(AutoDefine.class);

        String suffix = autoDefine.suffix();
        if (suffix.isEmpty()) {
            suffix = mybatisPlusExtProcessConfig.get(ConfigurationKey.ENTITY_DEFINE_SUFFIX);
        }
        String className = classElement.getSimpleName().toString() + suffix;

        String packageName = autoDefine.packageName();
        if(packageName.isEmpty()) {
            packageName = mybatisPlusExtProcessConfig.get(ConfigurationKey.ENTITY_DEFINE_PACKAGE_NAME);
        }
        String entityPackageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        packageName = getTargetPackageName(entityPackageName, packageName);
        try {
            JavaFileObject file = this.filer.createSourceFile(packageName + "." + className);
            try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                writer.println("package " + packageName + ";");
                writer.println();
                writer.println("public interface " + className + " {");
                for (String field : fields) {
                    writer.println("    String " + field + " = \"" + field + "\";");
                }
                writer.println("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
