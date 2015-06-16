/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.config;

import static com.netsol.adagent.util.beans.BaseData.arrayIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.coalesce;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.IOUtil;
import com.netsol.adagent.util.beans.Pair;

/**
 * Base class for configuration objects.
 */
public abstract class Config {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:39 Config.java NSI";

    private Log logger;
    private final Properties properties;

    /**
     * Constructor.
     */
    protected Config(String configFileName) {
        InputStream inputStream = null;
        try {
            properties = new Properties();

            try {
                // Attempt to load first from an embedded configuration file.
                inputStream = getClass().getClassLoader().getResourceAsStream(configFileName);
                properties.load(inputStream);
            }
            catch (Exception ex1) {
                try {
                    // Then from an external configuration file.
                    inputStream = new FileInputStream(configFileName);
                    properties.load(inputStream);
                }
                catch (Exception ex2) {
                    throw new IllegalArgumentException(configFileName);
                }
            }
        }
        finally {
            IOUtil.close(inputStream);
        }
    }

    /**
     * Constructor.
     */
    protected Config(Properties properties) {
        this.properties = properties;
    }

    /**
     * Return the java.util.Properties object.
     */
    public Properties getProperties() {
        return properties;
    }

    public void setLogger(Log logger) {
        this.logger = logger;
    }

    /**
     * Return the configuration value for the specified key.
     * Return null if the value is not found.
     */
    public String get(String key) {
        if (key != null) {
            String property = properties.getProperty(key.trim());
            if (property != null) {
                return property.trim();
            }
        }

        warn("Key not found: " + key);

        return null;
    }

    /**
     * Return the boolean configuration value for the specified key.
     * Return false if the value is not found.
     */
    public boolean getBoolean(String key) {
        return Converter.getBoolean(get(key));
    }

    /**
     * Return the boolean configuration value for the specified key.
     * Return null if the value is not found.
     */
    public Boolean getBooleanObject(String key) {
        return Converter.getBooleanObject(get(key));
    }

    /**
     * Return the double configuration value for the specified key.
     * Return 0 if the value is not found.
     */
    public double getDouble(String key) {
        return Converter.getDouble(get(key));
    }

    /**
     * Return the double configuration value for the specified key.
     * Return null if the value is not found.
     */
    public Double getDoubleObject(String key) {
        return Converter.getDoubleObject(get(key));
    }

    /**
     * Return the float configuration value for the specified key.
     * Return 0 if the value is not found.
     */
    public float getFloat(String key) {
        return Converter.getFloat(get(key));
    }

    /**
     * Return the float configuration value for the specified key.
     * Return null if the value is not found.
     */
    public Float getFloatObject(String key) {
        return Converter.getFloatObject(get(key));
    }

    /**
     * Return the integer configuration value for the specified key.
     * Return 0 if the value is not found.
     */
    public int getInt(String key) {
        return Converter.getInt(get(key));
    }

    /**
     * Return the integer configuration value for the specified key.
     * Return null if the value is not found.
     */
    public Integer getIntObject(String key) {
        return Converter.getIntObject(get(key));
    }

    /**
     * Return the list of configuration values for the specified key.
     * Return an empty list if the value is not found.
     */
    public List<String> getList(String key) {
        return getList(key, String.class);
    }

    /**
     * Return the list of configuration values for the specified key.
     * Return an empty list if the value is not found.
     */
    public <T> List<T> getList(String key, Class<T> classOfT) {
        return Converter.getList(get(key), classOfT);
    }

    public List<Pair<String, String>> getListOfPairs(String prefix, String firstSuffix, String secondSuffix) {
        return getListOfPairs(prefix, firstSuffix, String.class, secondSuffix, String.class);
    }

    public <First, Second> List<Pair<First, Second>> getListOfPairs(String prefix, String firstSuffix, Class<First> firstClass, String secondSuffix, Class<Second> secondClass) {
        List<Pair<First, Second>> list = new ArrayList<Pair<First, Second>>();
        for (int i = 0;; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix).append('.').append(i);
            if (firstSuffix != null) {
                sb.append('.').append(firstSuffix);
            }
            First first = get(sb.toString(), firstClass);
            if (first == null) {
                break;
            }
            sb = new StringBuilder();
            sb.append(prefix).append('.').append(i);
            if (secondSuffix != null) {
                sb.append('.').append(secondSuffix);
            }
            Second second = get(sb.toString(), secondClass);
            if (second == null) {
                break;
            }
            list.add(Pair.from(first, second));
        }

        return Collections.unmodifiableList(list);
    }

    /**
     * Return the long integer configuration value for the specified key.
     * Return 0 if the value is not found.
     */
    public long getLong(String key) {
        return Converter.getLong(get(key));
    }

    /**
     * Return the long integer configuration value for the specified key.
     * Return null if the value is not found.
     */
    public Long getLongObject(String key) {
        return Converter.getLongObject(get(key));
    }

    public Map<String, String> getTable(String key) {
        return getTable(key, String.class);
    }

    public <T> Map<String, T> getTable(String key, Class<T> classOfV, Class<?>... typeParameters) {
        String prefix = key + ".";
        int prefixLength = prefix.length();
        Map<String, T> table = new HashMap<String, T>();
        @SuppressWarnings("unchecked")
        Enumeration<String> propertyNames = (Enumeration<String>)properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = propertyNames.nextElement();
            if (propertyName.startsWith(prefix)) {
                T value = get(propertyName, classOfV, typeParameters);
                if (value != null) {
                    table.put(propertyName.substring(prefixLength), value);
                }
            }
        }

        return Collections.unmodifiableMap(table);
    }

    private <T> T get(String key, Class<T> classOfT, Class<?>... typeParameters) {
        return Converter.get(get(key), classOfT, typeParameters);
    }

    private void warn(String message) {
        if ((logger != null) && logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    /**
     * Convert values.
     */
    private static class Converter {
        @SuppressWarnings("unchecked")
        public static <T> T get(String stringValue, Class<T> classOfT, Class<?>... typeParameters) {
            T value = null;
            if (String.class.equals(classOfT)) {
                value = (T)stringValue;
            }
            else if (Boolean.class.equals(classOfT)) {
                value = (T)getBooleanObject(stringValue);
            }
            else if (Double.class.equals(classOfT)) {
                value = (T)getDoubleObject(stringValue);
            }
            else if (Float.class.equals(classOfT)) {
                value = (T)getFloatObject(stringValue);
            }
            else if (Integer.class.equals(classOfT)) {
                value = (T)getIntObject(stringValue);
            }
            else if (List.class.equals(classOfT)) {
                Class<?> classOfElement = arrayIsEmpty(typeParameters) ? String.class : typeParameters[0];
                value = (T)getList(stringValue, classOfElement);
            }
            else if (Long.class.equals(classOfT)) {
                value = (T)getLongObject(stringValue);
            }
            return value;
        }

        public static boolean getBoolean(String value) {
            return coalesce(getBooleanObject(value), Boolean.FALSE).booleanValue();
        }

        public static Boolean getBooleanObject(String value) {
            return (value == null) ? null : Boolean.valueOf(value);
        }

        public static double getDouble(String value) {
            return coalesce(getDoubleObject(value), Double.valueOf(0D)).doubleValue();
        }

        public static Double getDoubleObject(String value) {
            if (value != null) {
                try {
                    return Double.valueOf(value);
                }
                catch (NumberFormatException ex) {}
            }
            return null;
        }

        public static float getFloat(String value) {
            return coalesce(getFloatObject(value), Float.valueOf(0F)).floatValue();
        }

        public static Float getFloatObject(String value) {
            if (value != null) {
                try {
                    return Float.valueOf(value);
                }
                catch (NumberFormatException ex) {}
            }
            return null;
        }

        public static int getInt(String value) {
            return coalesce(getIntObject(value), Integer.valueOf(0)).intValue();
        }

        public static Integer getIntObject(String value) {
            if (value != null) {
                try {
                    return Integer.valueOf(value);
                }
                catch (NumberFormatException ex) {}
            }
            return null;
        }

        public static <T> List<T> getList(String value, Class<T> classOfT) {
            if (value != null) {
                List<T> list = new ArrayList<T>();
                int offset = -1;
                int count = 0;
                char[] charArray = value.toCharArray();
                for (int i = 0; i < charArray.length; i++) {
                    switch (charArray[i]) {
                    case ' ':
                        if (offset != -1) {
                            list.add(get(new String(charArray, offset, count), classOfT));
                            offset = -1;
                            count = 0;
                        }
                        break;

                    default:
                        if (offset == -1) {
                            offset = i;
                        }
                        count++;
                        break;
                    }
                }

                if (offset != -1) {
                    list.add(get(new String(charArray, offset, count), classOfT));
                }

                return Collections.unmodifiableList(list);
            }

            return Collections.emptyList();
        }

        public static long getLong(String value) {
            return coalesce(getLongObject(value), Long.valueOf(0L)).longValue();
        }

        public static Long getLongObject(String value) {
            if (value != null) {
                try {
                    return Long.valueOf(value);
                }
                catch (NumberFormatException ex) {}
            }
            return null;
        }
    }
}
