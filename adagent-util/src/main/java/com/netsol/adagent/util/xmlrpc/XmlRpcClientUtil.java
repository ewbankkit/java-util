/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.xmlrpc;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.netsol.adagent.util.A1;
import com.netsol.adagent.util.AccessibleObjectUtil;
import com.netsol.adagent.util.F1;

/**
 * XML-RPC client utilities.
 */
public final class XmlRpcClientUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:38 XmlRpcClientUtil.java NSI";

    /**
     *  Convert an XML-RPC response object to a usefully typed object.
     */
    public static <T> T fromResponse(Object object, final T t) throws Exception {
        if (object instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>)object).entrySet()) {
                Object key = entry.getKey();
                if (key instanceof String) {
                    String fieldName = (String)key;
                    Field field = null;
                    try {
                        field = t.getClass().getField(fieldName);
                    }
                    catch (NoSuchFieldException ex) {
                        // Ignore unmapped fields.
                        continue;
                    }

                    Object value = entry.getValue();
                    if (value instanceof Object[]) {
                        Class<?> fieldType = field.getType();
                        if (fieldType.isArray()) {
                            Class<?> componentType = fieldType.getComponentType();
                            if (componentType != Object.class) {
                                // Attempt array coercion.
                                Object[] from = (Object[])value;
                                int length  = from.length;
                                Object array = Array.newInstance(componentType, length);
                                for (int i = 0; i < length; i++) {
                                    Array.set(array, i, from[i]);
                                }

                                value = array;
                            }
                        }
                    }
                    else if (value instanceof Map<?, ?>) {
                        // Nested object.
                        Object nestedObject = AccessibleObjectUtil.withToggledAccessibility(field, new F1<Field, Object>() {
                            @Override
                            public Object apply(Field field) throws Exception {
                                return field.get(t);
                            }});
                        value = fromResponse(value, nestedObject);
                    }

                    final Object valueToSet = value;
                    AccessibleObjectUtil.withToggledAccessibility(field, new A1<Field>() {
                        @Override
                        public void apply(Field field) throws Exception {
                            field.set(t, valueToSet);
                        }});
                }
            }
        }

        return t;
    }

    /**
     *  Convert a usefully typed object to an XML-RPC request object.
     */
    public static Map<String, Object> toRequest(final Object object) throws Exception {
        Map<String, Object> request = new HashMap<String, Object>();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isSynthetic()) {
                // e.g. this$0
                continue;
            }

            Object value = AccessibleObjectUtil.withToggledAccessibility(field, new F1<Field, Object>() {
                @Override
                public Object apply(Field field) throws Exception {
                    return field.get(object);
                }});
            Class<?> fieldType = field.getType();
            if (fieldType.isArray()) {
                // Convert arrays that aren't Object[] to Object[].
                Class<?> componentType = fieldType.getComponentType();
                if (componentType != Object.class) {
                    int length = Array.getLength(value);
                    Object[] array = new Object[length];
                    for (int i = 0; i < length; i++) {
                        array[i] = Array.get(value, i);
                    }

                    value = array;
                }
            }

            if (value != null) {
                String fieldName = field.getName();
                request.put(fieldName, value);
            }
        }

        return request;
    }
}
