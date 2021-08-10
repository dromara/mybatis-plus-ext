package com.tangzc.mpe.core;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.tangzc.mpe.annotation.entity.DefaultValue;
import com.tangzc.mpe.annotation.entity.OptionDate;
import com.tangzc.mpe.annotation.entity.OptionUser;
import com.tangzc.mpe.annotation.handler.AutoFillHandler;
import com.tangzc.mpe.core.base.BaseEntity;
import com.tangzc.mpe.core.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据审计插件
 *
 * @author don
 */
@Slf4j
//@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {

        optionFill(metaObject, FieldFill.INSERT);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        optionFill(metaObject, FieldFill.UPDATE);
    }

    private void optionFill(MetaObject metaObject, FieldFill option) {

        Object object = metaObject.getOriginalObject();
        Class<?> clazz = object.getClass();

        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> fields = new ArrayList<>(Arrays.asList(declaredFields));
        for (Class<?> superClass = clazz.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            declaredFields = superClass.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
        }

        List<Field> fieldList = fields.stream()
                .filter(field -> metaObject.hasSetter(field.getName()))
                .filter(field -> {
                    TableField annotation = AnnotationUtils.findAnnotation(field, TableField.class);

                    return annotation != null &&
                            (annotation.fill() == option || annotation.fill() == FieldFill.INSERT_UPDATE);
                })
                .collect(Collectors.toList());

        fill(metaObject, object, clazz, fieldList);
    }

    public void fill(MetaObject metaObject, Object object, Class<?> clazz, List<Field> fieldList) {

        Now now = new Now();

        fieldList.forEach(field -> {

            // 默认值
            setDefaultVale(metaObject, field);

            // 操作人
            setOptionUser(metaObject, object, clazz, field);

            // 操作时间
            setOptionDate(metaObject, clazz, field, now);
        });
    }

    private void setDefaultVale(MetaObject metaObject, Field field) {

        DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(field, DefaultValue.class);
        if (defaultValue != null) {
            boolean canSet = this.getFieldValByName(field.getName(), metaObject) == null;
            if (canSet) {
                Object newVal = convert(field, defaultValue);
                this.setFieldValByName(field.getName(), newVal, metaObject);
            }
        }
    }

    private void setOptionUser(MetaObject metaObject, Object object, Class<?> clazz, Field field) {

        OptionUser optionUser = AnnotatedElementUtils.getMergedAnnotation(field, OptionUser.class);
        if (optionUser != null) {

            // 判断原来值为null，或者覆盖选项为true
            boolean canSet = this.getFieldValByName(field.getName(), metaObject) == null || optionUser.override();

            if (canSet) {

                Object userInfo = null;

                AutoFillHandler instance = getAutoFillHandler(optionUser.value());
                if (instance != null) {
                    userInfo = instance.getVal(object, clazz, field);
                }

                // 如果当前未取到信息，不设置
                if (userInfo != null) {
                    this.setFieldValByName(field.getName(), userInfo, metaObject);
                }
            }
        }
    }

    /**
     * 缓存AutoFillHandler，同时寻找
     */
    private AutoFillHandler getAutoFillHandler(Class<? extends AutoFillHandler> autoFillHandler) {

        try {
            return SpringContextUtil.getApplicationContext().getBean(autoFillHandler);
        } catch (NoUniqueBeanDefinitionException ignore) {
            throw new RuntimeException("发现了多个" + autoFillHandler.getName() + "的实现，请保持spring中只有一个实例。");
        } catch (NoSuchBeanDefinitionException ignore) {
            if (autoFillHandler.isInterface()) {
                log.warn("没有找到{}的实现，操作人信息无法自动填充。", autoFillHandler.getName());
            } else {
                log.warn("{}需要注册到spring，不然操作人信息无法自动填充。", autoFillHandler.getName());
            }
        }
        return null;
    }

    private void setOptionDate(MetaObject metaObject, Class<?> clazz, Field field, Now now) {

        OptionDate optionDate = AnnotatedElementUtils.getMergedAnnotation(field, OptionDate.class);
        if (optionDate != null) {

            boolean canSet = this.getFieldValByName(field.getName(), metaObject) == null
                    || optionDate.override();

            if (canSet) {
                Class<?> type = field.getType();

                // 针对BaseEntity的泛型日期类型
                if (BaseEntity.class.isAssignableFrom(clazz)) {
                    String typeName = field.getGenericType().getTypeName();
                    switch (typeName) {
                        case "ID_TYPE":
                            type = ReflectionKit.getSuperClassGenericType(clazz, 0);
                            break;
                        case "TIME_TYPE":
                            type = ReflectionKit.getSuperClassGenericType(clazz, 1);
                            break;
                        default:
                    }
                }

                Object nowDate = Optional.ofNullable(now.now(type, optionDate.format()))
                        .orElseThrow(() -> new RuntimeException("类：" + clazz.toString() + "的字段：" + field.getName()
                                + "的类型不支持。仅支持String、Long、long、Date、LocalDate、LocalDateTime"));

                this.setFieldValByName(field.getName(), nowDate, metaObject);
            }
        }
    }

    /**
     * 默认值转化
     *
     * @param field        默认值字段
     * @param defaultValue 默认值注解
     * @return 默认值
     */
    private Object convert(Field field, DefaultValue defaultValue) {

        String value = defaultValue.value();
        String format = defaultValue.format();
        Class<?> type = field.getType();
        Map<Class<?>, Function<String, Object>> convertFuncMap = new HashMap<Class<?>, Function<String, Object>>(16) {{
            put(String.class, value -> value);
            put(Long.class, Long::parseLong);
            put(long.class, Long::parseLong);
            put(Integer.class, Integer::parseInt);
            put(int.class, Integer::parseInt);
            put(Boolean.class, Boolean::parseBoolean);
            put(boolean.class, Boolean::parseBoolean);
            put(Double.class, Double::parseDouble);
            put(double.class, Double::parseDouble);
            put(Float.class, Float::parseFloat);
            put(float.class, Float::parseFloat);
            put(BigDecimal.class, BigDecimal::new);
            put(Date.class, value -> {
                try {
                    return new SimpleDateFormat(format).parse(value);
                } catch (ParseException e) {
                    throw new RuntimeException("日期格式" + format + "与值" + value + "不匹配！");
                }
            });
            put(LocalDate.class, value -> {
                try {
                    return LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
                } catch (Exception e) {
                    throw new RuntimeException("日期格式" + format + "与值" + value + "不匹配！");
                }
            });
            put(LocalDateTime.class, value -> {
                try {
                    return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
                } catch (Exception e) {
                    throw new RuntimeException("日期格式" + format + "与值" + value + "不匹配！");
                }
            });
        }};

        Function<String, Object> convertFunc = convertFuncMap.getOrDefault(type, val -> {
            if (type.isEnum()) {
                Object[] enumConstants = type.getEnumConstants();
                if (enumConstants.length > 0) {
                    for (Object enumConstant : enumConstants) {
                        if (Objects.equals(val, enumConstant.toString())) {
                            return enumConstant;
                        }
                    }
                }
                throw new RuntimeException("默认值" + val + "与枚举" + type.getName() + "不匹配！");
            } else {
                return val;
            }
        });
        return convertFunc.apply(value);
    }

    /**
     * 框架内部使用，定义当前时间的时间对象
     */
    private static class Now {

        private final LocalDateTime localDateTime = LocalDateTime.now();
        private final LocalDate localDate = this.localDateTime.toLocalDate();
        private final Date date = Date.from(this.localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        private final Long timestamp = this.date.getTime();

        public Object now(Class<?> type, String format) {

            if (type == String.class) {
                return this.localDateTime.format(DateTimeFormatter.ofPattern(format));
            }

            if (type == long.class || type == Long.class) {
                return timestamp;
            }

            if (type == Date.class) {
                return date;
            }

            if (type == LocalDate.class) {
                return localDate;
            }

            if (type == LocalDateTime.class) {
                return localDateTime;
            }

            return null;
        }
    }
}
