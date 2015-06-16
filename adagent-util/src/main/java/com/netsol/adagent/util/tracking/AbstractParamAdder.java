/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import static com.netsol.adagent.util.beans.BaseData.stringIsBlank;
import static com.netsol.adagent.util.beans.BaseData.stringIsNotBlank;
import static com.netsol.adagent.util.beans.BaseData.stringIsNotEmpty;

import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.beans.Pair;

/* package-private */ abstract class AbstractParamAdder extends AbstractParamManipulator implements ParamAdder {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:31 AbstractParamAdder.java NSI";

    /**
     * Add parameters to the specified URL and return the new URL.
     * The URL is assumed NOT to be URL encoded.
     */
    public final String addParams(String url) {
        return addParams(url, DEFAULT_STRATEGY);
    }

    /**
     * Add parameters to the specified URL and return the new URL.
     * The URL is URL encoded.
     */
    public final String addUrlEncodedParams(String url, String characterEncoding) {
        return addParams(url, getUrlEncodedStrategy(characterEncoding));
    }

    /**
     * Add parameters to the specified URL and return the new URL.
     * Use the specified strategy.
     */
    private String addParams(String url, Strategy strategy) {
        if (stringIsBlank(url)) {
            return url;
        }

        StringBuilder sb = new StringBuilder();
        String hostNamePlusPort = TrackingUtil.extractHostNamePlusPort(url);
        if (stringIsNotEmpty(hostNamePlusPort)) {
            sb.append(TrackingUtil.extractProtocolPlusDelimiter(url));
            sb.append(hostNamePlusPort);
        }
        Pair<String, String> pathAndParameters = TrackingUtil.extractPathAndParameters(url);
        String path = pathAndParameters.getFirst();
        if (stringIsNotBlank(path)) {
            sb.append(path);
        }
        String parameters = pathAndParameters.getSecond();
        QueryString queryString = stringIsBlank(parameters) ? new QueryString() : strategy.newQueryString(parameters);
        for (Pair<String, String> paramAndValue : getAddableParamsAndValues()) {
            queryString.add(paramAndValue.getFirst(), paramAndValue.getSecond());
        }
        String newParameters = strategy.toString(queryString);
        if (stringIsNotBlank(newParameters)) {
            sb.append('?').append(newParameters);
        }

        return sb.toString();
    }

    /**
     * Return the parameters and values which should be added.
     */
    protected abstract Iterable<Pair<String, String>> getAddableParamsAndValues();
}
