package org.dromara.mpe.processer.builder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.squareup.javapoet.ClassName;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoMapper;
import org.dromara.mpe.processer.config.ConfigurationKey;
import org.dromara.mpe.processer.config.MybatisPlusExtProcessConfig;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MapperBuilder extends BaseBuilder {

    private final Elements elementUtils;

    public MapperBuilder(Filer filer, Messager messager, Types typeUtils, Elements elementUtils, String projectRoot, MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig) {
        super(filer, messager, elementUtils, projectRoot, mybatisPlusExtProcessConfig);
        this.elementUtils = elementUtils;
    }


    public String buildMapper(TypeElement element, AutoMapper autoMapper) {

        String entityPackageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
        String entityName = element.getSimpleName().toString();

        String suffix = getMapperSuffix(autoMapper);
        String mapperName = getTargetName(autoMapper.value(), entityName, suffix);
        String packageName = getValueOrDefault(autoMapper.packageName(), ConfigurationKey.MAPPER_PACKAGE_NAME);
        String mapperPackageName = getTargetPackageName(element, packageName);

        // 检查文件已经被创建了，自动跳过
        String filePath = mapperPackageName + "." + mapperName;

        if (isExist(mapperPackageName, mapperName)) {
            return filePath;
        }

        String dsAnnoImport = null;
        String dsAnno = null;
        if (autoMapper.withDSAnnotation()) {
            String dsName = null;
            Table table = element.getAnnotation(Table.class);
            String dsAnnoClassName = "com.baomidou.dynamic.datasource.annotation.DS";
            if (table != null && !table.dsName().isEmpty()) {
                dsName = table.dsName();
            } else {
                try {
                    Class<? extends Annotation> dsClass = (Class<? extends Annotation>) Class.forName(dsAnnoClassName);
                    Annotation ds = element.getAnnotation(dsClass);
                    // ds 是 Annotation 对象
                    Method valueMethod = ds.annotationType().getMethod("value");
                    Object value = valueMethod.invoke(ds);
                    dsName = (String) value;
                } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException ignore) {
                }
                if (dsName == null) {
                    warn(entityPackageName + "." + entityName + "缺少@Table的dsName配置，无法为" + mapperPackageName + "." + mapperName + "添加@DS ");
                }
            }
            if (dsName != null) {
                dsAnnoImport = "import " + dsAnnoClassName + ";";
                dsAnno = "@DS(\"" + dsName + "\")";
            }
        }

        ClassName mapperSuperclassName = getMapperSuperclassName(autoMapper);

        List<String> lines = Arrays.asList(
                "package " + mapperPackageName + ";",
                "",
                "import " + mapperSuperclassName.canonicalName() + ";",
                dsAnnoImport,
                "import " + entityPackageName + "." + entityName + ";",
                "import org.apache.ibatis.annotations.Mapper;",
                "",
                dsAnno,
                "@Mapper",
                "public interface " + mapperName + " extends " + mapperSuperclassName.simpleName() + "<" + entityName + "> {",
                "}"
        );

        writeToFile(filePath, lines);

        return filePath;
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

    private String getMapperSuffix(AutoMapper autoMapper) {
        String suffix = autoMapper.suffix();
        if ("".equals(suffix)) {
            suffix = this.mybatisPlusExtProcessConfig.get(ConfigurationKey.MAPPER_SUFFIX);
        }
        return suffix;
    }
}
