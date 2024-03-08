package com.tangzc.mpe.demo.autotable.mysql;

import com.tangzc.autotable.annotation.TableIndex;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Component
@RestController
@RequestMapping("notice")
public class NoticeController {

    @Resource
    private NoticeRepository noticeRepository;

    @GetMapping("test")
    public void test() {
        System.out.println("-----------------");
        noticeRepository.lambdaQuery().eq(Notice::getId, "123").list();
        System.out.println("-----------------");
    }

    public static void main(String[] args) {

        TableIndex annotationWithDefaultValues = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(TableIndex.class);

        System.out.println(annotationWithDefaultValues.name());
    }

    public static class AnnotationDefaultValueHelper {

        public static <A extends Annotation> A getAnnotationWithDefaultValues(Class<A> annotationType) {
            Map<String, Object> defaultValues = getDefaultValues(annotationType);
            return createAnnotationInstance(annotationType, defaultValues);
        }

        private static <A extends Annotation> Map<String, Object> getDefaultValues(Class<A> annotationType) {
            Map<String, Object> defaultValues = new HashMap<>();
            Method[] declaredMethods = annotationType.getDeclaredMethods();

            for (Method method : declaredMethods) {
                if (method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                    Object defaultValue = method.getDefaultValue();
                    defaultValues.put(method.getName(), defaultValue);
                }
            }

            return defaultValues;
        }

        @SuppressWarnings("unchecked")
        private static <A extends Annotation> A createAnnotationInstance(Class<A> annotationType, Map<String, Object> values) {
            InvocationHandler handler = new AnnotationInvocationHandler(annotationType, values);
            return (A) Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, handler);
        }

        private static class AnnotationInvocationHandler implements InvocationHandler {
            private final Class<? extends Annotation> annotationType;
            private final Map<String, Object> values;

            AnnotationInvocationHandler(Class<? extends Annotation> annotationType, Map<String, Object> values) {
                this.annotationType = annotationType;
                this.values = new HashMap<>(values);
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if (values.containsKey(methodName)) {
                    return values.get(methodName);
                }
                return method.invoke(this, args);
            }
        }
    }
}
