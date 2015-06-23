//
// Kit's Java Utils.
//

package com.capitalonelabs.eucalyptus.ledger;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Returns a string representation of an object using reflection.
 */
public final class ReflectiveRepresentation {
    private static final String                     DATE_TIME_FORMAT    =
        "EEE MMM dd HH:mm:ss zzz yyyy";
    private static final DateTimeFormatter          DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    // Stops infinitely recursive calls.
    private static final ThreadLocal<Stack<Parent>> STACK               =
        new ThreadLocal<Stack<Parent>>() {
            @Override
            final protected Stack<Parent> initialValue() {
            return new Stack<>();
        }
        };

    private final Object object;

    /**
     * Constructor.
     */
    private ReflectiveRepresentation(Object object) {
        this.object = object;
    }

    /**
     * Returns a string representation of the specified object.
     */
    public static String toString(Object object) {
        return new ReflectiveRepresentation(object).toString();
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            int depth = 0;
            Object parentObject = null;
            Stack<Parent> stack = STACK.get();
            if (!stack.isEmpty()) {
                Parent parent = stack.peek();
                depth = parent.depth;
                parentObject = parent.object;
            }
            try {
                append(depth, sb, parentObject, null, object);
            }
            finally {
                if (stack.isEmpty()) {
                    STACK.remove();
                }
            }
        }
        catch (Exception ignored) {}

        return sb.toString();
    }

    /**
     * Appends the specified field.
     */
    private static void append(int depth, Appendable appendable, Object parent, CharSequence fieldName, Object fieldValue) throws IOException {
        if (fieldValue == null) {
            appendField(depth, appendable, fieldName, "<NULL>");
            return;
        }

        Class<?> classOfField = fieldValue.getClass();
        if (classOfField.isArray()) {
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
        else if (fieldValue instanceof Optional) {
            Optional<?> optional = ((Optional<?>)fieldValue);
            if (optional.isPresent()) {
                append(depth, appendable, fieldValue, fieldName, optional.get());
            }
            else {
                appendField(depth, appendable, fieldName, "<MISSING>");
            }
        }
        else if (fieldValue instanceof Either) {
            Either<?, ?> either = ((Either<?, ?>)fieldValue);
            if (either.isLeft()) {
                append(depth, appendable, fieldValue, fieldName, either.getLeft());
            }
            else {
                append(depth, appendable, fieldValue, fieldName, either.getRight());
            }
        }
        else if (fieldValue instanceof Enumeration) {
            final Enumeration<?> enumeration = (Enumeration<?>)fieldValue;
            Iterator<?> iterator = new Iterator<Object>() {
                @Override
                public boolean hasNext() {
                    return enumeration.hasMoreElements();
                }

                @Override
                public Object next() {
                    return enumeration.nextElement();
                }
            };
            append(depth, appendable, fieldValue, fieldName, iterator);
        }
        else if (fieldValue instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>)fieldValue;
            append(depth, appendable, fieldValue, fieldName, iterable.iterator());
        }
        else if (fieldValue instanceof Iterator) {
            Iterator<?> iterator = ((Iterator<?>)fieldValue);
            int i = 0;
            while (iterator.hasNext()) {
                append(depth, appendable, fieldValue, newIndexedFieldName(fieldName, i), iterator.next());
                i++;
            }
            if (i == 0) {
                appendField(depth, appendable, fieldName, "[]");
            }
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
            DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date;
            TimeZone timeZone;
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
        else if (fieldValue instanceof Instant) {
            Instant instant = (Instant)fieldValue;
            append(depth, appendable, fieldValue, fieldName, new Date(instant.toEpochMilli()));
        }
        else if (fieldValue instanceof TemporalAccessor) {
            TemporalAccessor temporal = (TemporalAccessor)fieldValue;
            appendField(depth, appendable, fieldName, DATE_TIME_FORMATTER.format(temporal));
        }
        else if (classOfField.isPrimitive() ||
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
        else if (fieldValue instanceof Pattern) {
            appendField(depth, appendable, fieldName, ((Pattern)fieldValue).pattern());
        }
        else if (fieldValue instanceof Currency) {
            appendField(depth, appendable, fieldName, ((Currency)fieldValue).getCurrencyCode());
        }
        else if (fieldValue instanceof InetAddress) {
            appendField(depth, appendable, fieldName, ((InetAddress)fieldValue).getHostAddress());
        }
        else {
            append(depth, appendable, parent, fieldName, fieldValue, classOfField);
        }
    }

    /**
     * Appends the specified field.
     */
    private static void append(int depth, Appendable appendable, Object parent, CharSequence fieldName, final Object fieldValue, Class<?> classOfField) throws IOException {
        CharSequence displayValue = null;
        boolean callToString = false;
        // Does the object implement its own toString method?
        // Check for self-recursion.
        if (fieldValue != parent) {
            try {
                classOfField.getDeclaredMethod("toString");
                callToString = true;
            }
            catch (NoSuchMethodException ignored) {}
        }
        if (!callToString) {
            StringBuilder sb = new StringBuilder();
            String className = classOfField.getName();

            Field[] fields = classOfField.getFields();
            if (fields != null) {
                for (Field field : fields) {
                    int fieldModifiers = field.getModifiers();
                    if (Modifier.isStatic(fieldModifiers) || !Modifier.isPublic(fieldModifiers) || field.isAnnotationPresent(Ignore.class)) {
                        continue;
                    }

                    try {
                        Object object = AccessibleObjects.withAccessibility(field,
                            (Field f) -> {
                                try {
                                    return field.get(fieldValue);
                                }
                                catch (IllegalAccessException ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                        if (sb.length() == 0) {
                            sb.append('\n');
                            appendSpaces(depth, sb);
                            sb.append("-----").append(className).append(" START-----");
                        }
                        append(depth + 1, sb, fieldValue, field.getName(), object);
                    }
                    catch (Exception ignored) {}
                }
            }

            Method[] methods = classOfField.getMethods();
            if (methods != null) {
                for (Method method : methods) {
                    int methodModifiers = method.getModifiers();
                    if (Modifier.isStatic(methodModifiers)) {
                        continue;
                    }

                    Class<?> returnType = method.getReturnType();
                    if (!Modifier.isPublic(methodModifiers) ||
                        method.isAnnotationPresent(Ignore.class) ||
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
                        !"getClass".equals(methodName) &&
                        // Don't display certain fields of thrown objects.
                        !((fieldValue instanceof Throwable) && ("getLocalizedMessage".equals(methodName) || "getStackTrace".equals(methodName)))) {
                        prefixLength = 3;
                    }
                    else if (methodName.startsWith("is") && (Boolean.TYPE.equals(returnType) || Boolean.class.equals(returnType))) {
                        prefixLength = 2;
                    }
                    if (prefixLength > 0) {
                        try {
                            Object object = AccessibleObjects.withAccessibility(method,
                                (Method m) -> {
                                    try {
                                        return method.invoke(fieldValue);
                                    }
                                    catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }
                                });
                            if (sb.length() == 0) {
                                sb.append('\n');
                                appendSpaces(depth, sb);
                                sb.append("-----").append(className).append(" START-----");
                            }
                            append(depth + 1, sb, fieldValue, methodName.substring(prefixLength), object);
                        }
                        catch (Exception ignored) {}
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
            Stack<Parent> stack = STACK.get();
            stack.push(new Parent(depth, fieldValue));
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
     * Appends the specified field name and value.
     */
    private static void appendField(int depth, Appendable appendable, CharSequence fieldName, CharSequence fieldValue) throws IOException {
        boolean fieldNameIsEmpty = StringUtils.isEmpty(fieldName);
        boolean fieldValueIsEmpty = StringUtils.isEmpty(fieldValue);

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
     * Appends spaces appropriate for the specified depth.
     */
    private static void appendSpaces(int depth, Appendable appendable) throws IOException {
        for (int i = 0; i < (depth * 2); i++) {
            appendable.append(' ');
        }
    }

    /**
     * Returns a new indexed field name for the specified base field name and
     * index.
     */
    private static CharSequence newIndexedFieldName(CharSequence baseFieldName, int index) {
        StringBuilder sb = StringUtils.isEmpty(baseFieldName) ? new StringBuilder() : new StringBuilder(baseFieldName);
        sb.append('[').append(index).append(']');
        return sb;
    }

    /**
     * Indicates that the annotated field or method should be ignored when
     * building the representation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface Ignore {}

    /**
     * Represents a parent instance on the stack.
     */
    private static final class Parent {
        public final int    depth;
        public final Object object;

        public Parent(int depth, Object object) {
            this.depth = depth;
            this.object = object;
        }
    }
}
