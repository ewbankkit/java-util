/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import static com.github.ewbankkit.util.beans.BaseData.toIterable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.github.ewbankkit.util.F1;
import com.github.ewbankkit.util.AccessibleObjectUtil;

/**
 * Returns a string representation of an object using reflection.
 */
/* package-private */ class ReflectiveRepresentation {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:06 ReflectiveRepresentation.java NSI";

    private static final ThreadLocal<Stack<Pair<Integer, Object>>> CURRENT_CONTEXT = new ThreadLocal<Stack<Pair<Integer, Object>>>() {
        @Override
        final protected Stack<Pair<Integer, Object>> initialValue() {
            return new Stack<Pair<Integer, Object>>();
        }
    };

    private final Object object;

    /**
     * Constructor.
     */
    public ReflectiveRepresentation(Object object) {
        this.object = object;
    }

    /**
     * Return a string representation of this object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            int depth = 0;
            Object parent = null;
            Stack<Pair<Integer, Object>> stack = CURRENT_CONTEXT.get();
            if (!stack.isEmpty()) {
                Pair<Integer, Object> pair = stack.peek();
                depth = pair.getFirst().intValue();
                parent = pair.getSecond();
            }
            append(depth, sb, parent, null, object);
        }
        catch (Exception ex) {}

        return sb.toString();
    }

    /**
     * Append the specified field.
     */
    @SuppressWarnings("unchecked")
    private static void append(int depth, Appendable appendable, Object parent, CharSequence fieldName, Object fieldValue) throws IOException {
        if (fieldValue == null) {
            appendField(depth, appendable, fieldName, "NULL");
            return;
        }

        Class<?> clazz = fieldValue.getClass();
        if (clazz.isArray()) {
            if (fieldValue instanceof boolean[]) {
                appendField(depth, appendable, fieldName, Arrays.toString((boolean[])fieldValue));
            }
            else if (fieldValue instanceof byte[]) {
                appendField(depth, appendable, fieldName, Arrays.toString((byte[])fieldValue));
            }
            else if (fieldValue instanceof char[]) {
                appendField(depth, appendable, fieldName, Arrays.toString((char[])fieldValue));
            }
            else if (fieldValue instanceof double[]) {
                appendField(depth, appendable, fieldName, Arrays.toString((double[])fieldValue));
            }
            else if (fieldValue instanceof float[]) {
                appendField(depth, appendable, fieldName, Arrays.toString((float[])fieldValue));
            }
            else if (fieldValue instanceof int[]) {
                appendField(depth, appendable, fieldName, Arrays.toString((int[])fieldValue));
            }
            else if (fieldValue instanceof long[]) {
                appendField(depth, appendable, fieldName, Arrays.toString((long[])fieldValue));
            }
            else if (fieldValue instanceof short[]) {
                appendField(depth, appendable, fieldName, Arrays.toString((short[])fieldValue));
            }
            else if ((fieldValue instanceof BigDecimal[]) ||
                      (fieldValue instanceof BigInteger[]) ||
                      (fieldValue instanceof Boolean[]) ||
                      (fieldValue instanceof Byte[]) ||
                      (fieldValue instanceof Character[]) ||
                      (fieldValue instanceof Double[]) ||
                      (fieldValue instanceof Enum[]) ||
                      (fieldValue instanceof Float[]) ||
                      (fieldValue instanceof Integer[]) ||
                      (fieldValue instanceof Long[]) ||
                      (fieldValue instanceof Short[]) ||
                      (fieldValue instanceof String[])) {
                appendField(depth, appendable, fieldName, Arrays.toString((Object[])fieldValue));
            }
            else {
                int length = Array.getLength(fieldValue);
                for (int i = 0; i < length; i++) {
                    Object element = Array.get(fieldValue, i);
                    append(depth, appendable, fieldValue, newIndexedFieldName(fieldName, i), element);
                }
            }
        }
        else if (fieldValue instanceof Iterable) {
            if ((fieldValue instanceof Collection) && ((Collection<?>)fieldValue).isEmpty()) {
                appendField(depth, appendable, fieldName, "[]");
            }
            else {
                int i = 0;
                for (Object element : (Iterable<?>)fieldValue) {
                    append(depth, appendable, fieldValue, newIndexedFieldName(fieldName, i), element);
                    i++;
                }
            }
        }
        else if (fieldValue instanceof Enumeration) {
            append(depth, appendable, fieldValue, fieldName, BaseData.toIterable((Enumeration<?>) fieldValue));
        }
        else if (fieldValue instanceof Map) {
            append(depth, appendable, fieldValue, fieldName, ((Map<?, ?>)fieldValue).entrySet());
        }
        else if (fieldValue instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)fieldValue;
            appendField(depth, appendable, fieldName, null);
            append(depth + 1, appendable, fieldValue, "Key", entry.getKey());
            append(depth + 1, appendable, fieldValue, "Value", entry.getValue());
        }
        else if ((fieldValue instanceof Calendar) || (fieldValue instanceof Date)) {
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date date = null;
            TimeZone timeZone = null;
            if (fieldValue instanceof Calendar) {
                Calendar calendar = (Calendar)fieldValue;
                date = calendar.getTime();
                timeZone = calendar.getTimeZone();
            }
            else {
                date = (Date)fieldValue;
                timeZone = TimeZone.getDefault();
            }
            dateFormat.setTimeZone(timeZone);
            appendField(depth, appendable, fieldName, dateFormat.format(date));
        }
        else if (clazz.isPrimitive() ||
                 (fieldValue instanceof BigDecimal) ||
                 (fieldValue instanceof BigInteger) ||
                 (fieldValue instanceof Boolean) ||
                 (fieldValue instanceof Byte) ||
                 (fieldValue instanceof Character) ||
                 (fieldValue instanceof Double) ||
                 (fieldValue instanceof Enum) ||
                 (fieldValue instanceof Float) ||
                 (fieldValue instanceof Integer) ||
                 (fieldValue instanceof Long) ||
                 (fieldValue instanceof Short) ||
                 (fieldValue instanceof String)) {
            appendField(depth, appendable, fieldName, fieldValue.toString());
        }
        else if (fieldValue instanceof Tuple) {
            Tuple tuple = (Tuple)fieldValue;
            int arity = tuple.arity();
            for (int i = 0; i < arity; i++) {
                append(depth, appendable, fieldValue, newIndexedFieldName(fieldName, i), tuple.get(i));
            }
        }
        else if (fieldValue instanceof Pattern) {
            appendField(depth, appendable, fieldName, ((Pattern)fieldValue).pattern());
        }
        else {
            append(depth, appendable, parent, fieldName, fieldValue, clazz);
        }
    }

    /**
     * Append the specified field.
     */
    private static void append(int depth, Appendable appendable, Object parent, CharSequence fieldName, final Object fieldValue, Class<?> clazz) throws IOException {
        CharSequence displayValue = null;
        boolean callToString = false;
        // Does the object implement its own toString method?
        // Check for self-recursion.
        if (fieldValue != parent) {
            try {
                clazz.getDeclaredMethod("toString");
                callToString = true;
            }
            catch (NoSuchMethodException ex) {}
        }
        if (!callToString) {
            StringBuilder sb = new StringBuilder();
            String className = clazz.getName();

            Field[] fields = clazz.getFields();
            if (fields != null) {
                for (Field field : fields) {
                    int fieldModifiers = field.getModifiers();
                    if (Modifier.isStatic(fieldModifiers) ||
                        !Modifier.isPublic(fieldModifiers) ||
                        field.isAnnotationPresent(NotInToString.class)) {
                        continue;
                    }

                    try {
                        Object object = AccessibleObjectUtil.withToggledAccessibility(field, new F1<Field, Object>() {
                            @Override
                            public Object apply(Field field) throws Exception {
                                return field.get(fieldValue);
                            }});
                        if (sb.length() == 0) {
                            sb.append('\n');
                            appendSpaces(depth, sb);
                            sb.append("-----").append(className).append(" START-----");
                        }
                        append(depth + 1, sb, fieldValue, field.getName(), object);
                    }
                    catch (Exception ex) {}
                }
            }

            Method[] methods = clazz.getMethods();
            if (methods != null) {
                for (Method method : methods) {
                    int methodModifiers = method.getModifiers();
                    if (Modifier.isStatic(methodModifiers)) {
                        continue;
                    }

                    Class<?> returnType = method.getReturnType();
                    if (!Modifier.isPublic(methodModifiers) ||
                        method.isAnnotationPresent(NotInToString.class) ||
                        method.isBridge() ||
                        method.isSynthetic() ||
                        // Returns void.
                        Void.TYPE.equals(returnType) ||
                        // Has parameters.
                        (method.getParameterTypes().length > 0)) {
                        continue;
                    }

                    String methodName = method.getName();
                    int prefixLength = 0;
                    if (methodName.startsWith("get") &&
                        // Don't display certain fields of thrown objects.
                        !((fieldValue instanceof Throwable) &&
                          (methodName.equals("getLocalizedMessage") || methodName.equals("getStackTrace"))) &&
                        !methodName.equals("getClass") &&
                        !methodName.startsWith("getAxis") &&
                        !methodName.startsWith("getXsd")) {
                        prefixLength = 3;
                    }
                    else if (methodName.startsWith("is") &&
                              (Boolean.TYPE.equals(returnType) || Boolean.class.equals(returnType))) {
                        prefixLength = 2;
                    }
                    if (prefixLength > 0) {
                        try {
                            Object object = AccessibleObjectUtil.withToggledAccessibility(method, new F1<Method, Object>() {
                                @Override
                                public Object apply(Method method) throws Exception {
                                    return method.invoke(fieldValue);
                                }});
                            if (sb.length() == 0) {
                                sb.append('\n');
                                appendSpaces(depth, sb);
                                sb.append("-----").append(className).append(" START-----");
                            }
                            append(depth + 1, sb, fieldValue, methodName.substring(prefixLength), object);
                        }
                        catch (Exception ex) {}
                    }
                }
            }

            if (sb.length() == 0) {
                callToString = true;
            }
            else {
                sb.append('\n');
                appendSpaces(depth, sb);
                sb.append("-----").append(className).append(" END-------");
                displayValue = sb;
            }
        }
        if (callToString) {
            Stack<Pair<Integer, Object>> stack = CURRENT_CONTEXT.get();
            stack.push(Pair.from(Integer.valueOf(depth), fieldValue));
            try {
                displayValue = fieldValue.toString();
            }
            finally {
                stack.pop();
            }
        }
        appendField(depth, appendable, fieldName, displayValue);
    }

    /**
     * Append the specified field name and value.
     */
    private static void appendField(int depth, Appendable appendable, CharSequence fieldName, CharSequence fieldValue) throws IOException {
        boolean fieldNameIsEmpty = BaseData.stringIsEmpty(fieldName);
        boolean fieldValueIsEmpty = BaseData.stringIsEmpty(fieldValue);

        if (!fieldNameIsEmpty || fieldValueIsEmpty || (fieldValue.charAt(0) != '\n')) {
            appendable.append('\n');
        }
        appendSpaces(depth, appendable);
        if (!fieldNameIsEmpty) {
            appendable.append(fieldName).append(':');
            if ((!fieldValueIsEmpty && fieldValue.charAt(0) != '\n')) {
                appendable.append(' ');
            }
        }
        if (!fieldValueIsEmpty) {
            appendable.append(fieldValue);
        }
    }

    /**
     * Append spaces appropriate for the specified depth.
     */
    private static void appendSpaces(int depth, Appendable appendable) throws IOException {
        for (int i = 0; i < (depth * 2); i++) {
            appendable.append(' ');
        }
    }

    /**
     * Return a new indexed field name for the specified base field name and index.
     */
    private static CharSequence newIndexedFieldName(CharSequence baseFieldName, int index) {
        StringBuilder sb = BaseData.stringIsEmpty(baseFieldName) ? new StringBuilder() : new StringBuilder(baseFieldName);
        sb.append('[').append(index).append(']');
        return sb;
    }
}
