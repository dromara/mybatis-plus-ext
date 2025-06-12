package org.dromara.mpe.processer.builder;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.squareup.javapoet.ClassName;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoRepository;
import org.dromara.mpe.processer.config.ConfigurationKey;
import org.dromara.mpe.processer.config.MybatisPlusExtProcessConfig;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.List;

public class RepositoryBuilder extends BaseBuilder {

    private final Elements elementUtils;
    private final MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig;

    public RepositoryBuilder(Filer filer, Messager messager, Types typeUtils, Elements elementUtils, MybatisPlusExtProcessConfig mybatisPlusExtProcessConfig, MapperBuilder mapperBuilder) {
        super(filer, messager, elementUtils, mybatisPlusExtProcessConfig);
        this.elementUtils = elementUtils;
        this.mybatisPlusExtProcessConfig = mybatisPlusExtProcessConfig;
    }

    public void buildRepository(TypeElement element, AutoRepository autoRepository, String fullMapperName) {
        /* 获取Entity的类名和包名 */
        String entityPackageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
        String entityName = element.getSimpleName().toString();

        /* 获取Repository的类名和包名 */
        String suffix = autoRepository.suffix();
        if ("".equals(suffix)) {
            suffix = this.mybatisPlusExtProcessConfig.get(ConfigurationKey.REPOSITORY_SUFFIX);
        }
        String repositoryName = getTargetName(autoRepository.value(), entityName, suffix);
        String customPackageName = autoRepository.packageName();
        if (customPackageName.isEmpty()) {
            customPackageName = mybatisPlusExtProcessConfig.get(ConfigurationKey.REPOSITORY_PACKAGE_NAME);
        }
        String repositoryPackageName = getTargetPackageName(element, customPackageName);

        // 检查文件已经被创建了，自动跳过
        if (isExist(repositoryPackageName, repositoryName)) {
            return;
        }

        /* 获取mapper的类名和包名 */
        int endIndex = fullMapperName.lastIndexOf(".");
        String mapperPackageName = fullMapperName.substring(0, endIndex);
        String mapperName = fullMapperName.substring(endIndex + 1);

        String dsAnnoImport = null;
        String dsAnno = null;
        if (autoRepository.withDSAnnotation()) {
            String dsName = null;
            Table table = element.getAnnotation(Table.class);
            if (table != null && !table.dsName().isEmpty()) {
                dsName = table.dsName();
            } else {
                DS ds = element.getAnnotation(DS.class);
                if (ds != null && !ds.value().isEmpty()) {
                    dsName = ds.value();
                } else {
                    warn(entityPackageName + "." + entityName + "缺少@Table的dsName配置，无法为" + repositoryPackageName + "." + repositoryName + "添加@DS ");
                }
            }
            if (dsName != null) {
                dsAnnoImport = "import com.baomidou.dynamic.datasource.annotation.DS;";
                dsAnno = "@DS(\"" + dsName + "\")";
            }
        }

        ClassName repositorySuperclassName = getRepositorySuperclassName(autoRepository);

        String implExtInterface = "";
        String importExtInterface = "";
        try {
            Class<?> iBaseRepositoryClass = Class.forName("org.dromara.mpe.bind.repository.IBaseRepository");
            importExtInterface = "import " + iBaseRepositoryClass.getName() + ";";
            implExtInterface = " implements " + iBaseRepositoryClass.getSimpleName() + "<" + entityName + ">";
        } catch (ClassNotFoundException ignored) {
        }

        List<String> lines = Arrays.asList(
                "package " + repositoryPackageName + ";",
                "",
                importExtInterface,
                dsAnnoImport,
                "import " + repositorySuperclassName.canonicalName() + ";",
                "import " + entityPackageName + "." + entityName + ";",
                "import " + mapperPackageName + "." + mapperName + ";",
                "import org.springframework.stereotype.Repository;",
                "",
                dsAnno,
                "@Repository",
                "public class " + repositoryName + " extends " + repositorySuperclassName.simpleName() + "<" + mapperName + ", " + entityName + ">" + implExtInterface + " {",
                "}"
        );

        writeToFile(repositoryPackageName + "." + repositoryName, lines);
    }

    private ClassName getRepositorySuperclassName(AutoRepository autoRepository) {

        String baseRepositoryClassName = autoRepository.superclassName();
        if (baseRepositoryClassName.isEmpty()) {
            baseRepositoryClassName = mybatisPlusExtProcessConfig.get(ConfigurationKey.REPOSITORY_SUPERCLASS_NAME);
        }
        if (!baseRepositoryClassName.isEmpty()) {
            int lastIndexOf = baseRepositoryClassName.lastIndexOf(".");
            String baseRepositoryPackageName = baseRepositoryClassName.substring(0, lastIndexOf);
            String baseRepositoryName = baseRepositoryClassName.substring(lastIndexOf + 1);
            return ClassName.get(baseRepositoryPackageName, baseRepositoryName);
        }

        return ClassName.get(CrudRepository.class);
    }
}
