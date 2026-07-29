package org.dromara.mpe.processer.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationKeyTest {

    @Test
    void globalEnable_shouldHaveCorrectKeyAndDefault() {
        assertEquals("processor.global.enable", ConfigurationKey.GLOBAL_ENABLE.getConfigKey());
        assertEquals("false", ConfigurationKey.GLOBAL_ENABLE.getDefaultValue());
    }

    @Test
    void stopPropagation_shouldHaveCorrectKeyAndDefault() {
        assertEquals("processor.stopPropagation", ConfigurationKey.STOP_PROPAGATION.getConfigKey());
        assertEquals("false", ConfigurationKey.STOP_PROPAGATION.getDefaultValue());
    }

    @Test
    void entityDefineSuffix_shouldHaveCorrectKeyAndDefault() {
        assertEquals("processor.entityDefineSuffix", ConfigurationKey.ENTITY_DEFINE_SUFFIX.getConfigKey());
        assertEquals("Define", ConfigurationKey.ENTITY_DEFINE_SUFFIX.getDefaultValue());
    }

    @Test
    void mapperSuffix_shouldHaveCorrectKeyAndDefault() {
        assertEquals("processor.mapperSuffix", ConfigurationKey.MAPPER_SUFFIX.getConfigKey());
        assertEquals("Mapper", ConfigurationKey.MAPPER_SUFFIX.getDefaultValue());
    }

    @Test
    void repositorySuffix_shouldHaveCorrectKeyAndDefault() {
        assertEquals("processor.repositorySuffix", ConfigurationKey.REPOSITORY_SUFFIX.getConfigKey());
        assertEquals("Repository", ConfigurationKey.REPOSITORY_SUFFIX.getDefaultValue());
    }

    @Test
    void allValues_shouldHaveNonNullKeyAndDefault() {
        for (ConfigurationKey key : ConfigurationKey.values()) {
            assertNotNull(key.getConfigKey(), key.name() + " configKey should not be null");
            assertNotNull(key.getDefaultValue(), key.name() + " defaultValue should not be null");
        }
    }

    @Test
    void valueOf_shouldWork() {
        assertEquals(ConfigurationKey.GLOBAL_ENABLE, ConfigurationKey.valueOf("GLOBAL_ENABLE"));
    }

    @Test
    void entityDefineStrictExtends_shouldDefaultToTrue() {
        assertEquals("true", ConfigurationKey.ENTITY_DEFINE_STRICT_EXTENDS.getDefaultValue());
    }
}
