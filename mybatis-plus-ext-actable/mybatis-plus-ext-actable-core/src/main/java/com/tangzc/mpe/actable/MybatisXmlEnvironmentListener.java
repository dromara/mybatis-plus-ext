package com.tangzc.mpe.actable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * 自动添加xml的包扫描路径
 * @author don
 */
public class MybatisXmlEnvironmentListener implements EnvironmentPostProcessor {

    private static final String MYBATIS_MAPPER_LOCATIONS = "mybatis.mapper-locations";
    private static final String MYBATIS_MAPPER_LOCATIONS_VALUE = "classpath:actable-mapper/CreateMysqlTablesMapper.xml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        if (WebApplicationType.NONE == application.getWebApplicationType()) {
            return;
        }

        String mapperLocations = environment.getProperty(MYBATIS_MAPPER_LOCATIONS);
        if(mapperLocations != null) {
            mapperLocations = mapperLocations + "," + MYBATIS_MAPPER_LOCATIONS_VALUE;
        } else {
            mapperLocations = "classpath:mapper/*.xml," + MYBATIS_MAPPER_LOCATIONS_VALUE;
        }

        Properties props = new Properties();
        props.put(MYBATIS_MAPPER_LOCATIONS, mapperLocations);
        environment.getPropertySources().addFirst(new PropertiesPropertySource("mybatis_custom_properties", props));
    }
}
