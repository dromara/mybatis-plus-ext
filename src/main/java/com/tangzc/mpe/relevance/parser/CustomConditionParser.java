package com.tangzc.mpe.relevance.parser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 自定义条件解析器
 * @author don
 */
public class CustomConditionParser {

    private static final Map<String, Method> READ_METHOD_CACHE = new ConcurrentHashMap<>();

    private static final Pattern FIELD_PATTERN = Pattern.compile("(?<=\\{)[\\w_\\d]+(?=\\})");

    public static <BEAN> String parse(BEAN bean, String customCondition) {

//        Matcher matcher = FIELD_PATTERN.matcher(customCondition);
//
//        Map<String, Object> fieldMap = new HashMap<>(matcher.groupCount());
//        while (matcher.find()) {
//            try {
//                String fieldName = matcher.group();
//                Method readMethod = READ_METHOD_CACHE.computeIfAbsent(
//                        bean.getClass().getName() + "." + fieldName,
//                        key -> BeanClassUtil.getReadMethod(bean.getClass(), fieldName)
//                );
//                Object fieldVal = readMethod.invoke(bean);
//                fieldMap.put(fieldName, fieldVal);
//            } catch (Exception e) {
//                throw new RuntimeException("自定义条件表达式，解析字段出错", e);
//            }
//        }
//
//        for (Map.Entry<String, Object> fieldEntry : fieldMap.entrySet()) {
//            String fieldName = fieldEntry.getKey();
//            Object value = fieldEntry.getValue();
//            String fieldVal;
//            if (value instanceof String) {
//                fieldVal = "'" + value + "'";
//            } else {
//                fieldVal = value.toString();
//            }
//            customCondition = customCondition.replace("{" + fieldName + "}", fieldVal);
//        }

        return customCondition;
    }
}
