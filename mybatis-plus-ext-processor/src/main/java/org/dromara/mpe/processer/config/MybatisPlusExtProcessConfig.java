/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.dromara.mpe.processer.config;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * mpe Mapper生成配置。
 */
public class MybatisPlusExtProcessConfig {

    /**
     * 配置文件名。
     */
    private static final String APT_FILE_NAME = "mybatis-plus-ext.config";

    /**
     * mybatis-plus-ext.properties
     */
    protected final Properties properties = new Properties();

    private static MybatisPlusExtProcessConfig INSTANCE = null;

    public static synchronized MybatisPlusExtProcessConfig getInstance(Filer filer) {
        if (INSTANCE == null) {
            INSTANCE = new MybatisPlusExtProcessConfig(filer);
        }
        return INSTANCE;
    }

    private MybatisPlusExtProcessConfig(Filer filer) {
        try {
            //target/classes/
            FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "mybatis-plus-ext");
            File classPathFile = new File(resource.toUri()).getParentFile();

            String projectRootPath = FileUtil.getProjectRootPath(classPathFile, 10);

            List<File> aptConfigFiles = new ArrayList<>();

            while (projectRootPath != null && classPathFile != null
                    && projectRootPath.length() <= classPathFile.getAbsolutePath().length()) {
                File aptConfig = new File(classPathFile, APT_FILE_NAME);
                if (aptConfig.exists()) {
                    aptConfigFiles.add(aptConfig);
                }
                classPathFile = classPathFile.getParentFile();
            }


            for (File aptConfigFile : aptConfigFiles) {
                try (InputStream stream = Files.newInputStream(aptConfigFile.toPath());
                     Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                    Properties config = new Properties();
                    config.load(reader);

                    Set<Object> keySet = config.keySet();
                    for (Object key : keySet) {
                        if (!properties.containsKey(key)) {
                            properties.put(key, config.getProperty((String) key));
                        }
                    }
                    // 终止向上继续查找父类的配置
                    String stopPropagationConfigKey = ConfigurationKey.STOP_PROPAGATION.getConfigKey();
                    if (keySet.contains(stopPropagationConfigKey)) {
                        String stopPropagation = String.valueOf(config.getProperty(stopPropagationConfigKey));
                        if ("true".equalsIgnoreCase(stopPropagation) || "on".equalsIgnoreCase(stopPropagation)) {
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(ConfigurationKey key) {
        String value = properties.getProperty(key.getConfigKey(), key.getDefaultValue());
        System.out.println("配置项：" + key.getConfigKey() + "=" + value);
        return value;
    }

}
