//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.json;

import com.capitalone.cardcompanion.common.io.FileProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * JSON configuration utilities.
 */
public final class JsonConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonConfig.class);

    private JsonConfig() {}

    /**
     * Returns a simple map from the application configuration.
     * The iteration order is the order of keys in the file.
     */
    public static Optional<? extends Map<String, String>> simpleMapFromConfig(String prefix) {
        Preconditions.checkNotNull(prefix);

        return mapFromConfig(prefix, new Function<JsonNode, String>() {
            @Nullable
            @Override
            public String apply(@Nullable JsonNode jsonNode) {
                assert jsonNode != null;
                if (jsonNode.isTextual()) {
                    return jsonNode.textValue();
                }
                return null;
            }
        });
    }

    /**
     * Returns a map of arrays from the application configuration.
     * The iteration order is the order of keys in the file.
     */
    public static Optional<? extends Map<String, String[]>> arrayMapFromConfig(String prefix) {
        Preconditions.checkNotNull(prefix);

        return mapFromConfig(prefix, new Function<JsonNode, String[]>() {
            @Nullable
            @Override
            public String[] apply(@Nullable JsonNode jsonNode) {
                assert jsonNode != null;
                if (jsonNode.isArray()) {
                    List<String> strings = new ArrayList<>(jsonNode.size());
                    for (JsonNode element : jsonNode) {
                        strings.add(element.asText());
                    }
                    if (!strings.isEmpty()) {
                        return strings.toArray(new String[strings.size()]);
                    }
                }
                return null;
            }
        });
    }

    /**
     * Returns a simple map from the application configuration.
     * The iteration order is the order of keys in the file.
     */
    private static <T> Optional<? extends Map<String, T>> mapFromConfig(String prefix, Function<JsonNode, T> function) {
        Optional<FileProperties> optionalJsonFileProperties = FileProperties.fromConfig(prefix);
        if (!optionalJsonFileProperties.isPresent()) {
            return Optional.absent();
        }

        Builder<String, T> mapBuilder = ImmutableMap.builder();
        try (InputStream is = optionalJsonFileProperties.get().getByteSource().openStream()) {
            JsonNode tree = new ObjectMapper().readTree(is);
            Iterator<Entry<String, JsonNode>> fields = tree.fields();
            while (fields.hasNext()) {
                Entry<String, JsonNode> field = fields.next();
                T t = function.apply(field.getValue());
                if (t != null) {
                    mapBuilder.put(field.getKey(), t);
                }
            }

            return Optional.of(mapBuilder.build());
        }
        catch (IOException ex) {
            LOGGER.warn("Unable to read JSON configuration", ex);
        }

        return Optional.absent();
    }
}
