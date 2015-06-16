/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import static com.netsol.adagent.util.beans.BaseData.stringIsBlank;
import static com.netsol.adagent.util.beans.BaseData.stringIsNotBlank;
import static com.netsol.adagent.util.beans.BaseData.stringIsNotEmpty;

import java.util.regex.Pattern;

import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.beans.Pair;

/* package-private */ abstract class AbstractParamStripper extends AbstractParamManipulator implements ParamStripper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:32 AbstractParamStripper.java NSI";

    private final boolean ignoreCase;

    /**
     * Constructor.
     */
    protected AbstractParamStripper(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    /**
     * Strip parameters from the specified URL and return the new URL.
     * The URL is assumed NOT to be URL encoded.
     */
    public final String stripParams(String url) {
        return stripParams(url, DEFAULT_STRATEGY);
    }

    /**
     * Strip parameters from the specified URL and return the new URL.
     * The URL is URL encoded.
     */
    public final String stripUrlEncodedParams(String url, String characterEncoding) {
        return stripParams(url, getUrlEncodedStrategy(characterEncoding));
    }

    /**
     * Strip parameters from the specified URL and return the new URL.
     * Use the specified strategy.
     */
    private String stripParams(String url, Strategy strategy) {
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
        if (stringIsNotBlank(parameters)) {
            QueryString queryString = strategy.newQueryString(parameters);
            if (ignoreCase) {
                queryString.removeIgnoreCase(getStrippableParams());
            }
            else {
                queryString.remove(getStrippableParams());
            }
            queryString.removeRegex(getStrippablePatterns());
            String newParameters = strategy.toString(queryString);
            if (stringIsNotBlank(newParameters)) {
                sb.append('?').append(newParameters);
            }
        }

        return sb.toString();
    }

    /**
     * Return the parameters which should be stripped.
     */
    protected abstract Iterable<String> getStrippableParams();
    protected abstract Iterable<Pattern> getStrippablePatterns();
}
