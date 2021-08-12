package com.tangzc.mpe.actable.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 通过包名获取class
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午5:55:18
 */
public class ClassTools {

    private static final Logger log = LoggerFactory.getLogger(ClassTools.class);

    /**
     * 从包package中获取所有的Class
     *
     * @param packs 扫描的包，支持多个","或者";"隔开
     * @return 该包下的class
     */
    public static Set<Class<?>> getClasses(String packs) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 拆成多个pack，支持多个
        String[] split = packs.split(",|;");
        for (String pack : split) {
            // 是否循环迭代
            boolean recursive = true;
            // 获取包的名字 并进行替换
            String packageName = pack;
            String packageDirName = packageName.replace('.', '/');
            // 定义一个枚举的集合 并进行循环来处理这个目录下的things
            Enumeration<URL> dirs;
            try {
                dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
                // 循环迭代下去
                while (dirs.hasMoreElements()) {
                    // 获取下一个元素
                    URL url = dirs.nextElement();
                    // 得到协议的名称
                    String protocol = url.getProtocol();
                    // 如果是以文件的形式保存在服务器上
                    if ("file".equals(protocol)) {
                        log.info("file类型的扫描:" + pack);
                        // 获取包的物理路径
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        // 以文件的方式扫描整个包下的文件 并添加到集合中
                        findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                    } else if ("jar".equals(protocol)) {
                        // 如果是jar包文件
                        // 定义一个JarFile
                        log.info("jar类型的扫描");
                        JarFile jar;
                        try {
                            // 获取jar
                            jar = ((JarURLConnection) url.openConnection()).getJarFile();
                            // 从此jar包 得到一个枚举类
                            Enumeration<JarEntry> entries = jar.entries();
                            // 同样的进行循环迭代
                            while (entries.hasMoreElements()) {
                                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                                JarEntry entry = entries.nextElement();
                                String name = entry.getName();
                                // 如果是以/开头的
                                if (name.charAt(0) == '/') {
                                    // 获取后面的字符串
                                    name = name.substring(1);
                                }
                                // 如果前半部分和定义的包名相同
                                if (name.startsWith(packageDirName)) {
                                    int idx = name.lastIndexOf('/');
                                    // 如果以"/"结尾 是一个包
                                    if (idx != -1) {
                                        // 获取包名 把"/"替换成"."
                                        packageName = name.substring(0, idx).replace('/', '.');
                                    }
                                    // 如果可以迭代下去 并且是一个包
                                    if ((idx != -1) || recursive) {
                                        // 如果是一个.class文件 而且不是目录
                                        if (name.endsWith(".class") && !entry.isDirectory()) {
                                            // 去掉后面的".class" 获取真正的类名
                                            String className = name.substring(packageName.length() + 1, name.length() - 6);
                                            try {
                                                // 添加到classes
                                                classes.add(Class.forName(packageName + '.' + className));
                                                log.info("读取到class:{}", packageName + '.' + className);
                                            } catch (ClassNotFoundException e) {
                                                log.error("添加用户自定义视图类错误 找不到此类的.class文件", e);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                            log.error("在扫描用户定义视图时从jar包获取文件出错", e);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("在扫描class包异常", e);
            }
        }
        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(
            String packageName,
            String packagePath,
            final boolean recursive,
            Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // classes.add(Class.forName(packageName + '.' +
                    // className));
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                    log.info("读取到class:{}", packageName + '.' + className);
                } catch (ClassNotFoundException e) {
                    log.error("添加用户自定义视图类错误 找不到此类的.class文件", e);
                }
            }
        }
    }

    /**
     * 取出list对象中的某个属性的值作为list返回
     *
     * @param objList
     * @param fieldName
     * @return
     */
    public static <T, E> List<E> getPropertyValueList(List<T> objList, String fieldName) {
        List<E> list = new ArrayList<E>();
        try {
            for (T object : objList) {
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                list.add((E) field.get(object));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * List中的string转换为大写
     *
     * @param list
     * @return
     */
    public static List<String> toLowerCase(List<String> list) {
        if (null == list || list.isEmpty()) {
            return list;
        }
        List<String> upperList = new ArrayList<>();
        for (String str : list) {
            upperList.add(str.toLowerCase());
        }
        return upperList;
    }

    public static String getBootPackage() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("main".equals(stackTraceElement.getMethodName())) {
                return ClassUtils.getPackageName(stackTraceElement.getClassName());
            }
        }
        throw new RuntimeException("未找到主默认包");
    }
}
