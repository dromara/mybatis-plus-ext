package org.dromara.mpe.autotable.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import org.dromara.autotable.adapter.mybatisplus.MybatisPlusAdapterConfig;
import org.dromara.autotable.core.AutoTableClassScanner;
import org.dromara.autotable.core.AutoTableMetadataAdapter;
import org.dromara.autotable.core.converter.JavaTypeToDatabaseTypeConverter;
import org.dromara.mpe.autotable.CustomDataSourceInfoExtractor;
import org.dromara.mpe.autotable.CustomJavaTypeToDatabaseTypeConverter;
import org.dromara.mpe.autotable.CustomRunStateCallback;
import org.dromara.mpe.autotable.DynamicDatasourceHandler;
import org.dromara.mpe.autotable.ExtClassScanner;
import org.dromara.mpe.autotable.ExtMetadataAdapter;
import org.dromara.mpe.autotable.IgnoreExt;
import org.dromara.mpe.magic.util.SpringContextUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * ext AutoTable 自动配置类。
 * <p>
 * 替代原有的 {@code MpeAutoTableAutoConfig}，使用 autotable 2.6.1 的 adapter 架构：
 * <ul>
 *   <li>通过 {@link MybatisPlusAdapterConfig} 传递 MP 配置（含 mapUnderscoreToCamelCase null 兜底）</li>
 *   <li>通过 {@link ExtMetadataAdapter}（继承 MybatisPlusMetadataAdapter）实现 ext 自定义注解支持，
 *       同时实现 {@link org.dromara.autotable.springboot.InitializeBeans} 确保提前初始化</li>
 *   <li>通过 {@link ExtClassScanner}（继承 MybatisPlusAutoTableClassScanner）扩展类扫描，
 *       支持 ext @Table 注解</li>
 *   <li>{@link DynamicDatasourceHandler} 通过 @Import 注册，保留其类级 @ConditionalOnClass
 *       和 @ConditionalOnProperty 条件</li>
 *   <li>修正遗漏：注册 CustomRunBeforeCallback 和 CustomRunAfterCallback</li>
 * </ul>
 * <p>
 * 本配置类通过 @AutoConfigureBefore 确保在 {@code AutoTableAutoConfig} 之前加载，
 * 使所有 ext Bean 在 AutoTableAutoConfig 构造器通过 ObjectProvider 收集时已就绪。
 *
 * @author don
 */
@Configuration
@ConditionalOnClass(name = "com.baomidou.mybatisplus.annotation.TableName")
@AutoConfigureBefore(name = "org.dromara.autotable.springboot.AutoTableAutoConfig")
@Import({SpringContextUtil.class, DynamicDatasourceHandler.class})
public class ExtAutoConfiguration {

    /**
     * MybatisPlusAdapterConfig Bean — ext 自行创建，处理 mapUnderscoreToCamelCase null 兜底。
     * <p>
     * 从 MP 的 {@link MybatisPlusProperties} 读取全局配置（表前缀、逻辑删除、大写模式等），
     * 转换为 autotable adapter 所需的 {@link MybatisPlusAdapterConfig}。
     * <p>
     * mapUnderscoreToCamelCase：未在 YAML 中显式配置时值为 null（Boolean 包装类型），
     * MP 运行时默认行为是 true。此处做 null 兜底：
     * {@code mapUnderscore == null || mapUnderscore} → null 时返回 true，对齐 MP 运行时行为。
     *
     * @param properties MP 自动配置属性（com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties）
     * @return adapter 配置
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusAdapterConfig.class)
    public MybatisPlusAdapterConfig mybatisPlusAdapterConfig(MybatisPlusProperties properties) {
        MybatisPlusAdapterConfig config = new MybatisPlusAdapterConfig();
        GlobalConfig.DbConfig dbConfig = properties.getGlobalConfig().getDbConfig();
        config.setTablePrefix(dbConfig.getTablePrefix());
        config.setLogicDeleteField(dbConfig.getLogicDeleteField());
        config.setLogicNotDeleteValue(dbConfig.getLogicNotDeleteValue());
        config.setCapitalMode(dbConfig.isCapitalMode());
        // mapUnderscoreToCamelCase null 兜底：未配置时默认 true（对齐 MP 运行时行为）
        Boolean mapUnderscore = properties.getConfiguration().getMapUnderscoreToCamelCase();
        config.setMapUnderscoreToCamelCase(mapUnderscore == null || mapUnderscore);
        return config;
    }

    /**
     * ExtMetadataAdapter Bean — ext 元数据适配器。
     * <p>
     * 继承 {@link org.dromara.autotable.adapter.mybatisplus.MybatisPlusMetadataAdapter}，
     * 实现 {@link org.dromara.autotable.springboot.InitializeBeans} 确保提前初始化。
     * 支持 ext 自定义注解（@Table/@Column/@ColumnId），未匹配时回退到 MP 原生注解。
     *
     * @param config            adapter 配置
     * @param ignoreExtProvider IgnoreExt 扩展钩子（ObjectProvider 延迟获取）
     * @return ext 元数据适配器
     */
    @Bean
    @ConditionalOnMissingBean(AutoTableMetadataAdapter.class)
    public ExtMetadataAdapter extMetadataAdapter(MybatisPlusAdapterConfig config,
            ObjectProvider<IgnoreExt> ignoreExtProvider) {
        return new ExtMetadataAdapter(config, ignoreExtProvider);
    }

    /**
     * ExtClassScanner Bean — ext 类扫描器。
     * <p>
     * 继承 {@link org.dromara.autotable.adapter.mybatisplus.MybatisPlusAutoTableClassScanner}，
     * 在父类已包含的注解基础上追加 ext @Table 注解。
     *
     * @return ext 类扫描器
     */
    @Bean
    @ConditionalOnMissingBean(AutoTableClassScanner.class)
    public ExtClassScanner extClassScanner() {
        return new ExtClassScanner();
    }

    /**
     * CustomJavaTypeToDatabaseTypeConverter Bean — Java 类型到数据库类型转换器。
     * <p>
     * 处理枚举类型（按字符串存储）和 JSON 类型字段（typeHandler 非 UnknownTypeHandler 时按字符串处理）。
     *
     * @return 类型转换器
     */
    @Bean
    public JavaTypeToDatabaseTypeConverter customJavaTypeToDatabaseTypeConverter() {
        return new CustomJavaTypeToDatabaseTypeConverter();
    }

    /**
     * CustomDataSourceInfoExtractor Bean — 动态数据源信息提取器。
     * <p>
     * 仅在 dynamic-datasource 库存在时注册，从 DynamicRoutingDataSource 中提取真实数据源信息。
     *
     * @return 数据源信息提取器
     */
    @Bean
    @ConditionalOnClass(name = "com.baomidou.dynamic.datasource.DynamicRoutingDataSource")
    public CustomDataSourceInfoExtractor customDataSourceInfoExtractor() {
        return new CustomDataSourceInfoExtractor();
    }

    /**
     * CustomRunBeforeCallback Bean — 建表执行前回调。
     * <p>
     * 设置 MP 拦截器忽略策略（租户、非法 SQL、阻塞攻击），
     * 防止建表 SQL 被 MP 插件拦截。
     *
     * @return 前置回调
     */
    @Bean
    public CustomRunStateCallback.CustomRunBeforeCallback customRunBeforeCallback() {
        return new CustomRunStateCallback.CustomRunBeforeCallback();
    }

    /**
     * CustomRunAfterCallback Bean — 建表执行后回调。
     * <p>
     * 清除 MP 拦截器忽略策略，恢复正常拦截行为。
     *
     * @return 后置回调
     */
    @Bean
    public CustomRunStateCallback.CustomRunAfterCallback customRunAfterCallback() {
        return new CustomRunStateCallback.CustomRunAfterCallback();
    }
}
