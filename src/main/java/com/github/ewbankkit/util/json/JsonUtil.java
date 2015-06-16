/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.json;

import com.google.gson.Gson;

/**
 * JSON utilities.
 */
public final class JsonUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:01 JsonUtil.java NSI";

    private static final Gson GSON = new Gson();

    private JsonUtil() {}

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return fromJson(json, classOfT, GSON);
    }

    public static <T> T fromJson(String json, Class<T> classOfT, Gson gson) {
        if (gson == null) {
            throw new IllegalArgumentException();
        }
        return gson.fromJson(json, classOfT);
    }

    public static String toJson(Object src) {
        return toJson(src, GSON);
    }

    public static String toJson(Object src, Gson gson) {
        if (gson == null) {
            throw new IllegalArgumentException();
        }
        return gson.toJson(src);
    }
}
