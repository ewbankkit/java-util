//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.netflix.config.DynamicPropertyFactory;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Application configuration.
 */
@ThreadSafe
public final class Config {
    private final Configuration configuration;

    private static final Logger           LOGGER              = LoggerFactory.getLogger(Config.class);
    private static       Optional<String> NAME                = Optional.absent();
    private static final Splitter         WHITESPACE_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();

    public static final Function<String, Optional<Integer>> GET_INTEGER = new Function<String, Optional<Integer>>() {
        @Nullable
        @Override
        public Optional<Integer> apply(@Nullable String s) {
            return getInstance().getInteger(s);
        }
    };
    public static final Function<String, Optional<String>> GET_STRING = new Function<String, Optional<String>>() {
        @Nullable
        @Override
        public Optional<String> apply(@Nullable String s) {
            return getInstance().getString(s);
        }
    };

    static {
        // Provide an optional hook for setting the config name from the command line. This is useful for testing.
        String configName = System.getProperty("config.name");
        if (configName != null) {
            Config.setName(configName);
        }
    }

    /**
     * Constructor.
     * Loads configuration from properties file.
     * Causes an initialization of the logging system.
     */
    private Config() {
        String name = getName();
        String environmentName = getEnvironmentName();
        String userName = getUserName();
        String propertiesFileName = String.format("%s-%s.properties", name, environmentName);

        AbstractConfiguration configuration = new BaseConfiguration(); // Empty.
        try {
            CompositeConfiguration compositeConfiguration = new CompositeConfiguration();

            if (userName != null) {
                String userProfileFileName = String.format("dev-profiles/%s-%s-%s.properties", name, userName.toLowerCase(), environmentName);

                try (InputStream devProfileIS = Config.class.getClassLoader().getResourceAsStream(userProfileFileName)) {
                    if (devProfileIS != null) {
                        PropertiesConfiguration devProfileConfig = new PropertiesConfiguration();
                        devProfileConfig.setListDelimiter(Character.MAX_VALUE);
                        devProfileConfig.load(devProfileIS);
                        compositeConfiguration.addConfiguration(devProfileConfig);
                    }
                }
                catch (IOException | ConfigurationException ex) {
                    LOGGER.info("Unable to load user profile: {}", userProfileFileName, ex);
                }
            }
            else {
                LOGGER.warn("User name is not present in system properties. User profiles will not be available.");
            }

            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(propertiesFileName);
            // We do our own list parsing.
            propertiesConfiguration.setListDelimiter(Character.MAX_VALUE);
            compositeConfiguration.addConfiguration(propertiesConfiguration);

            // Later values hide earlier ones.
            // Remove empty values.
            Iterator<String> iterator = compositeConfiguration.getKeys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = compositeConfiguration.getProperty(key);
                if (value instanceof Iterable) {
                    Object last = Iterables.getLast((Iterable<?>)value, null);
                    if (last != null) {
                        compositeConfiguration.setProperty(key, last);
                    }
                }
                if (compositeConfiguration.getString(key).isEmpty()) {
                    compositeConfiguration.clearProperty(key);
                }
            }

            // Configuration objects of this type can be read concurrently by multiple threads.
            configuration = compositeConfiguration;

            // Configure Archaius.
            DynamicPropertyFactory.initWithConfigurationSource(configuration);
        }
        catch (ConfigurationException ex) {
            LOGGER.warn("Unable to load configuration", ex);
        }

        this.configuration = configuration;
    }

    /**
     * Returns the single application configuration instance.
     */
    public static Config getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Gets the environment name. Default environment name is 'local'.
     */
    public static String getEnvironmentName() {
        return System.getProperty(String.format("%s.env", getName()), "local");
    }

    /**
     * Gets the configuration name. Default configuration name is 'orchestrator'.
     */
    public static String getName() {
        if (!NAME.isPresent()) {
            NAME = Optional.of("orchestrator");
        }
        return NAME.get();
    }

    /**
     * Returns name of currently logged user. Used for developer profile support
     */
    public static String getUserName() {
        return System.getProperty("user.name");
    }
    /**
     * Sets the configuration name. Default configuration name is 'orchestrator'.
     */
    public static void setName(String name) {
        Preconditions.checkNotNull(name);
        Preconditions.checkState(!NAME.isPresent());

        NAME = Optional.of(name);
    }

    /**
     * Returns an optional boolean configuration value for the specified key.
     */
    public Optional<BigDecimal> getBigDecimal(String key) {
        Preconditions.checkNotNull(key);

        return Optional.fromNullable(configuration.getBigDecimal(key));
    }

    /**
     * Returns an optional boolean configuration value for the specified key.
     */
    public Optional<Boolean> getBoolean(String key) {
        Preconditions.checkNotNull(key);

        return Optional.fromNullable(configuration.getBoolean(key, null));
    }

    /**
     * Returns a boolean configuration value for the specified key,
     * using the default value if none is present.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Preconditions.checkNotNull(key);

        return configuration.getBoolean(key, defaultValue);
    }

    /**
     * Returns an optional integer configuration value for the specified key.
     */
    public Optional<Integer> getInteger(String key) {
        Preconditions.checkNotNull(key);

        return Optional.fromNullable(configuration.getInteger(key, null));
    }

    /**
     * Returns an integer configuration value for the specified key,
     * using the default value if none is present.
     */
    public int getInteger(String key, int defaultValue) {
        Preconditions.checkNotNull(key);

        return configuration.getInt(key, defaultValue);
    }

    /**
     * Returns an optional long integer configuration value for the specified key.
     */
    public Optional<Long> getLong(String key) {
        Preconditions.checkNotNull(key);

        return Optional.fromNullable(configuration.getLong(key, null));
    }

    /**
     * Returns a long integer configuration value for the specified key,
     * using the default value if none is present.
     */
    public long getLong(String key, long defaultValue) {
        Preconditions.checkNotNull(key);

        return configuration.getLong(key, defaultValue);
    }

    /**
     * Returns an optional configuration value for the specified key.
     */
    public Optional<String> getString(String key) {
        Preconditions.checkNotNull(key);

        return Optional.fromNullable(configuration.getString(key));
    }

    /**
     * Returns a configuration value for the specified key,
     * using the default value if none is present.
     */
    public String getString(String key, String defaultValue) {
        Preconditions.checkNotNull(key);

        return configuration.getString(key, defaultValue);
    }

    /**
     * Returns an optional list of configuration values for the specified key.
     */
    public Optional<List<String>> getList(String key) {
        Preconditions.checkNotNull(key);

        String value = getString(key, null);
        if (value == null) {
            return Optional.absent();
        }

        List<String> list = ImmutableList.copyOf(toIterable(value));
        return Optional.of(list);
    }

    /**
     * Returns an optional map of configuration values for the specified key.
     */
    public Optional<Map<String, String>> getMap(String prefix) {
        Preconditions.checkNotNull(prefix);

        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Configuration subset = configuration.subset(prefix);
        Iterator<String> iterator = subset.getKeys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.put(key, subset.getString(key));
        }
        Map<String, String> map = builder.build();

        return map.isEmpty() ? Optional.<Map<String, String>>absent() : Optional.of(map);
    }

    /**
     * Returns an optional multimap of configuration values for the specified key.
     */
    public Optional<Multimap<String, String>> getMultimap(String prefix) {
        Preconditions.checkNotNull(prefix);

        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
        Configuration subset = configuration.subset(prefix);
        Iterator<String> iterator = subset.getKeys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.putAll(key, toIterable(subset.getString(key)));
        }
        Multimap<String, String> multimap = builder.build();

        return multimap.isEmpty() ? Optional.<Multimap<String, String>>absent() : Optional.of(multimap);
    }

    /**
     * Returns the properties.
     */
    public Properties getProperties() {
        // Take a copy.
        Properties properties = new Properties();
        Iterator<String> iterator = configuration.getKeys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            properties.put(key, configuration.getString(key));
        }

        return properties;
    }

    /**
     * Splits a value at whitespace.
     */
    public static Iterable<String> toIterable(String value) {
        Preconditions.checkNotNull(value);

        return WHITESPACE_SPLITTER.split(value);
    }

    /**
     * Returns configuration key prefixes.
     * Most derived name wins (is last).
     */
    public static <T extends U, U> Iterable<String> keyPrefixes(final Class<T> classOfT, final Class<U> classOfU) {
        Preconditions.checkNotNull(classOfT);
        Preconditions.checkNotNull(classOfU);

        List<String> prefixes = new ArrayList<>();
        for (Class<?> c = classOfT; !c.equals(classOfU.getSuperclass()); c = c.getSuperclass()) {
            String name = c.getSimpleName();
            char firstLetter = name.charAt(0);
            if (Character.isUpperCase(firstLetter)) {
                name = String.format("%s%s", String.valueOf(Character.toLowerCase(firstLetter)), name.substring(1));
            }
            prefixes.add(name);
        }
        Collections.reverse(prefixes);

        return prefixes;
    }

    /**
     * Returns the first value from a chain of configuration keys.
     */
    public static <T> Optional<T> getFirst(String key, Iterable<String> keyPrefixes, Function<String, Optional<T>> getter) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(keyPrefixes);
        Preconditions.checkNotNull(getter);

        for (String keyPrefix : keyPrefixes) {
            Optional<T> optionalValue = getter.apply(String.format("%s.%s", keyPrefix, key));
            assert optionalValue != null;
            if (optionalValue.isPresent()) {
                return optionalValue;
            }
        }
        return Optional.absent();
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public static final Config INSTANCE = new Config();
    }
}
