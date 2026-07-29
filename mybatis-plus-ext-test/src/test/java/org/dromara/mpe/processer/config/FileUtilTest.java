package org.dromara.mpe.processer.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @TempDir
    Path tempDir;

    // --- existsBuildFile ---

    @Test
    void existsBuildFile_pomXml_shouldReturnTrue() throws IOException {
        File dir = tempDir.toFile();
        new File(dir, "pom.xml").createNewFile();
        assertTrue(FileUtil.existsBuildFile(dir));
    }

    @Test
    void existsBuildFile_buildGradle_shouldReturnTrue() throws IOException {
        File dir = tempDir.toFile();
        new File(dir, "build.gradle").createNewFile();
        assertTrue(FileUtil.existsBuildFile(dir));
    }

    @Test
    void existsBuildFile_buildGradleKts_shouldReturnTrue() throws IOException {
        File dir = tempDir.toFile();
        new File(dir, "build.gradle.kts").createNewFile();
        assertTrue(FileUtil.existsBuildFile(dir));
    }

    @Test
    void existsBuildFile_noBuildFile_shouldReturnFalse() {
        File dir = tempDir.toFile();
        assertFalse(FileUtil.existsBuildFile(dir));
    }

    // --- isFromTestSource ---

    @Test
    void isFromTestSource_testSourcesPath_shouldReturnTrue() {
        assertTrue(FileUtil.isFromTestSource("/project/src/test-sources/Test.java"));
    }

    @Test
    void isFromTestSource_testAnnotationsPath_shouldReturnTrue() {
        assertTrue(FileUtil.isFromTestSource("/project/target/test-annotations/Test.java"));
    }

    @Test
    void isFromTestSource_mainSourcesPath_shouldReturnFalse() {
        assertFalse(FileUtil.isFromTestSource("/project/src/main/java/Test.java"));
    }

    // --- isAbsolutePath ---

    @Test
    void isAbsolutePath_unixAbsolute_shouldReturnTrue() {
        assertTrue(FileUtil.isAbsolutePath("/usr/local/bin"));
    }

    @Test
    void isAbsolutePath_windowsAbsolute_shouldReturnTrue() {
        assertTrue(FileUtil.isAbsolutePath("C:\\Users\\test"));
    }

    @Test
    void isAbsolutePath_relativePath_shouldReturnFalse() {
        assertFalse(FileUtil.isAbsolutePath("src/main/java"));
    }

    @Test
    void isAbsolutePath_null_shouldReturnFalse() {
        assertFalse(FileUtil.isAbsolutePath(null));
    }

    // --- getProjectRootPath ---

    @Test
    void getProjectRootPath_nullFile_shouldReturnNull() {
        assertNull(FileUtil.getProjectRootPath((File) null, 10));
    }

    @Test
    void getProjectRootPath_zeroDepth_shouldReturnNull() {
        assertNull(FileUtil.getProjectRootPath(tempDir.toFile(), 0));
    }

    @Test
    void getProjectRootPath_negativeDepth_shouldReturnNull() {
        assertNull(FileUtil.getProjectRootPath(tempDir.toFile(), -1));
    }
}
