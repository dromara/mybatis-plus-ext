package com.tangzc.mpe.autotable;

import com.tangzc.mpe.autotable.utils.ClassScanner;
import com.tangzc.mpe.autotable.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class ProfileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        Class<?> applicationClass = SpringContextUtil.getApplicationClass();
        EnableAutoTable enableAutoTable = applicationClass.getAnnotation(EnableAutoTable.class);
        if (enableAutoTable == null) {
            applicationClass = ClassScanner.scan(new String[]{SpringContextUtil.getBootPackage()}, EnableAutoTable.class).stream().findFirst().orElse(null);
            if (applicationClass != null) {
                enableAutoTable = applicationClass.getAnnotation(EnableAutoTable.class);
            }
        }

        if (enableAutoTable == null) {
            log.error("未找到EnableAutoTable声明，无法自动建表");
            return false;
        }

        Set<String> includeProfiles = Arrays.stream(enableAutoTable.activeProfile())
                .collect(Collectors.toSet());
        String[] properties = context.getEnvironment().getProperty(enableAutoTable.profileProperty(), String[].class);

        // 默认情况
        boolean isDefault = includeProfiles.size() == 1 && includeProfiles.contains("");
        if(isDefault) {
            return true;
        }

        if (properties != null && properties.length > 0) {
            Set<String> propertySet = Arrays.stream(properties).collect(Collectors.toSet());
            return includeProfiles.stream().anyMatch(propertySet::contains);
        }

        return false;
    }
}
