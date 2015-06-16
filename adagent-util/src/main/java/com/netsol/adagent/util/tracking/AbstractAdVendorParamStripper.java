/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import com.netsol.adagent.util.codes.AdUrlParameterName;

/* package-private */ abstract class AbstractAdVendorParamStripper extends SimpleParamStripper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:31 AbstractAdVendorParamStripper.java NSI";

    private final Iterable<Pattern> strippablePatterns;

    /**
     * Constructor.
     */
    protected AbstractAdVendorParamStripper(Collection<String> strippableParams) {
        this(strippableParams, null);
    }

    /**
     * Constructor.
     */
    protected AbstractAdVendorParamStripper(Collection<String> strippableParams, Iterable<Pattern> strippablePatterns) {
        super(getAllStrippableParams(strippableParams), false);
        this.strippablePatterns = strippablePatterns;
    }

    /**
     * Return all parameters which should be stripped.
     */
    private static Collection<String> getAllStrippableParams(Collection<String> strippableParams) {
        Collection<String> allStrippableParams = new ArrayList<String>(strippableParams);
        allStrippableParams.add(AdUrlParameterName.NS_AD_GROUP_ID);
        return Collections.unmodifiableCollection(allStrippableParams);
    }

    @Override
    protected final Iterable<Pattern> getStrippablePatterns() {
        return strippablePatterns;
    }
}
