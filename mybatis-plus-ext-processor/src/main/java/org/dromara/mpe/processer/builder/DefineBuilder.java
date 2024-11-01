package org.dromara.mpe.processer.builder;

import org.dromara.mpe.processer.annotation.AutoDefine;
import org.dromara.mpe.processer.config.ConfigurationKey;
import org.dromara.mpe.processer.config.MybatisPlusExtProcessConfig;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefineBuilder extends BaseBuilder {

    private final Types typeUtils;

    public DefineBuilder(Filer filer, Messager messager, Types typeUtils, Elements elementUtils, MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig) {
        super(filer, messager, elementUtils, mybatisPlusExtProcessConfig);
        this.typeUtils = typeUtils;
    }

    public void buildDefine(TypeElement classElement, AutoDefine autoDefine) {

        String suffix = getValueOrDefault(autoDefine.suffix(), ConfigurationKey.ENTITY_DEFINE_SUFFIX);
        String defineName = classElement.getSimpleName().toString() + suffix;

        String packageName = getValueOrDefault(autoDefine.packageName(), ConfigurationKey.ENTITY_DEFINE_PACKAGE_NAME);
        String definePackageName = getTargetPackageName(classElement, packageName);

        // 检查文件已经被创建了，自动跳过
        if (isExist(definePackageName, defineName)) {
            return;
        }

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

        List<String> lines = Arrays.asList(
                "package " + definePackageName + ";",
                "",
                "public interface " + defineName + " {",
                fields.stream().map(field -> "    String " + field + " = \"" + field + "\";").collect(Collectors.joining("\n")),
                "}"
        );

        writeToFile(definePackageName + "." + defineName, lines);
    }
}
