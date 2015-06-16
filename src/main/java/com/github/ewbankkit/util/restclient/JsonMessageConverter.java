/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.restclient;

import static com.github.ewbankkit.util.json.JsonUtil.fromJson;
import static com.github.ewbankkit.util.json.JsonUtil.toJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Standard JSON message converter.
 * Date format is "yyyy-MM-dd'T'HH:mm:ss.SSSZ".
 */
public final class JsonMessageConverter implements MessageConverter {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:27 JsonMessageConverter.java NSI";

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

    public <T> T fromString(String string, Class<T> classOfT) throws Exception {
        return fromJson(string, classOfT, gson);
    }

    public String getMediaType() {
        return "application/json";
    }

    public String toString(Object object) throws Exception {
        return toJson(object, gson);
    }
}
