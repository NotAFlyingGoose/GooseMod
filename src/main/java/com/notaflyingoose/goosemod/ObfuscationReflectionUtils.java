package com.notaflyingoose.goosemod;

import net.minecraft.world.item.SpawnEggItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ObfuscationReflectionUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T> T getStaticFieldOfType(Class<?> clazz, Class<T> type) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !field.getType().isAssignableFrom(type))
                continue;
            if (Modifier.isPrivate(field.getModifiers()))
                field.setAccessible(true);
            try {
                return (T) field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        LOGGER.warn("Could not find a static field in " + clazz.getName() + " which is assignable from " + type.getName());
        return null;
    }

    public static <T> T getPrivateStaticFieldOfType(Class<?> clazz, Class<T> type) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isPrivate(field.getModifiers()) || !field.getType().isAssignableFrom(type))
                continue;
            field.setAccessible(true);
            try {
                return (T) field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        LOGGER.warn("Could not find a private static field in " + clazz.getName() + " which is assignable from " + type.getName());
        return null;
    }

    public static <T> T getPublicStaticFieldOfType(Class<?> clazz, Class<T> type) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isPublic(field.getModifiers()) || !field.getType().isAssignableFrom(type))
                continue;
            try {
                return (T) field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        LOGGER.warn("Could not find a public static field in " + clazz.getName() + " which is assignable from " + type.getName());
        return null;
    }

}
