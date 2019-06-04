package com.sidney.coin.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectHelper {

    private static Logger logger = LoggerFactory.getLogger(ReflectHelper.class);

    /**
     * 利用反射获取指定对象的指定属性
     *
     * @param obj       目标对象
     * @param fieldName 目标属性
     * @return 目标属性的值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        Object result = null;
        Field field = ReflectHelper.getField(obj, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                result = field.get(obj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.error("反射获取对象属性值出错：{}", e);
            }
        }
        return result;
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     *
     * @param obj       目标对象
     * @param fieldName 目标属性
     * @return 目标字段
     */
    private static Field getField(Object obj, String fieldName) {
        Field field = null;
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
                logger.warn("not found field named {} at {}", fieldName, obj.getClass().getName());
            }
        }
        return field;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     *
     * @param obj        目标对象
     * @param fieldName  目标属性
     * @param fieldValue 目标值
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        Field field = ReflectHelper.getField(obj, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.error("反射设置对象属性出错：{}", e);
            }
        }
    }

    public static List<Field> getClassAnnotatedFields(Class clazz, Class<? extends Annotation> annotatedClazz) {
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotatedClazz)) {
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    public static List<Field> getClassAnnotatedFieldsUntil(Class<?> clazz, Class<? extends Annotation> annotatedClazz) {
        List<Field> annotatedFields = getClassAnnotatedFields(clazz, annotatedClazz);
        Class<?> superClass = clazz.getSuperclass();
        while (!superClass.isAssignableFrom(Object.class)) {
            annotatedFields.addAll(getClassAnnotatedFields(superClass, annotatedClazz));
            superClass = superClass.getSuperclass();
        }
        return annotatedFields;
    }

    public static List<Field> getFieldsUntil(Class<?> clazz, FieldSelectionCondition condition) {
        List<Field> fieldList = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (condition.isAcceptableField(declaredField)) {
                fieldList.add(declaredField);
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        while (!superClass.isAssignableFrom(Object.class)) {
            for (Field field : superClass.getDeclaredFields()) {
                if (condition.isAcceptableField(field)) {
                    fieldList.add(field);
                }
            }
            superClass = superClass.getSuperclass();
        }
        return fieldList;
    }

    @FunctionalInterface
    public interface FieldSelectionCondition {
        boolean isAcceptableField(Field field);
    }

}