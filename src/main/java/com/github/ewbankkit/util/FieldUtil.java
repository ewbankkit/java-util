/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import java.lang.reflect.Field;

public final class FieldUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:58 FieldUtil.java NSI";

    public static Field getField(Object object, String name) {
        return getField(object.getClass(), name);
    }

    /**
     * Return the specified field in the class or any superclass.
     * Return null if no such field exists.
     */
    public static Field getField(Class<?> clazz, String name) {
        try {
            while (clazz != null) {
                try {
                    return clazz.getDeclaredField(name);
                }
                catch (NoSuchFieldException ex) {}
                clazz = clazz.getSuperclass();
            }
        }
        catch (Exception ex) {}

        return null;
    }

    private FieldUtil() {}
}
