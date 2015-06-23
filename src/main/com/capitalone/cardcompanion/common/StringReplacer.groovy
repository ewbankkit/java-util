//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common

import com.capitalone.cardcompanion.common.json.JsonConfig
import com.google.common.base.Optional
import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMap.Builder

import javax.annotation.Nullable
import javax.annotation.concurrent.ThreadSafe
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Replace patterns in a string.
 */
@ThreadSafe
final class StringReplacer {
    private final Map<Pattern, String> patterns;

    /**
     * Constructor.
     */
    StringReplacer(String prefix) {
        Preconditions.checkNotNull prefix

        Builder<Pattern, String> mapBuilder = ImmutableMap.builder()
        Optional<? extends Map<String, String>> config = JsonConfig.simpleMapFromConfig prefix
        if (config.isPresent()) {
            config.get().each {
                String key, String value ->
                    mapBuilder.put(Pattern.compile(key, Pattern.CASE_INSENSITIVE), value)
            }
        }

        patterns = mapBuilder.build()
    }

    String replaceAll(@Nullable String original) {
        if (original != null) {
            for (def pattern in patterns) {
                Matcher matcher = (original =~ pattern.key)
                if (matcher) {
                    original = matcher.replaceAll pattern.value
                }
            }
        }
        return original
    }
}
