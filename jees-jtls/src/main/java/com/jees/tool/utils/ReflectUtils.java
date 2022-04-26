package com.jees.tool.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 反射辅助工具类
 *
 * @date 2022/4/25
 */
public class ReflectUtils {

    /**
     * java反射bean的get方法
     *
     * @param objectClass objectClass
     * @param fieldName fieldName
     * @return Method
     * @throws RuntimeException
     */
    public static Method getGetMethod(Class<?> objectClass, String fieldName) {
        StringBuilder sb = new StringBuilder();
        sb.append("get");
        sb.append(fieldName.substring(0, 1).toUpperCase(Locale.ROOT));
        sb.append(fieldName.substring(1));

        try {
            return objectClass.getMethod(sb.toString());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Reflect error!");
        }
    }

    /**
     * java反射bean的set方法
     *
     * @param objectClass objectClass
     * @param fieldName fieldName
     * @return Method
     * @throws RuntimeException
     */
    public static Method getSetMethod(Class<?> objectClass, String fieldName) {
        try {
            Class<?>[] parameterTypes = new Class<?>[1];
            Field field = objectClass.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            StringBuilder sb = new StringBuilder();
            sb.append("set");
            sb.append(fieldName.substring(0, 1).toUpperCase(Locale.ROOT));
            sb.append(fieldName.substring(1));
            return objectClass.getMethod(sb.toString(), parameterTypes);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException("Reflect error!");
        }
    }

    /**
     * 执行set方法
     *
     * @param obj 执行对象
     * @param fieldName 属性
     * @param value 值
     * @throws RuntimeException
     */
    public static void invokeSet(Object obj, String fieldName, Object value) {
        Method method = getSetMethod(obj.getClass(), fieldName);
        try {
            method.invoke(obj, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Reflect error!");
        }
    }

    /**
     * 执行get方法
     *
     * @param obj 执行对象
     * @param fieldName 属性
     * @return Object
     * @throws RuntimeException
     */
    public static Object invokeGet(Object obj, String fieldName) {
        Method method = getGetMethod(obj.getClass(), fieldName);
        try {
            return method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Reflect error!");
        }
    }

}
