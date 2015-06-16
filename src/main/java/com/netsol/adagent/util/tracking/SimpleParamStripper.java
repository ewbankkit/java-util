/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import static com.netsol.adagent.util.beans.BaseData.toIterable;

import java.util.regex.Pattern;

public class SimpleParamStripper extends AbstractParamStripper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:35 SimpleParamStripper.java NSI";

    private final Iterable<String> strippableParams;

    /**
     * Constructor.
     */
    public SimpleParamStripper(String[] strippableParams, boolean ignoreCase) {
        this(toIterable(strippableParams), ignoreCase);
    }

    /**
     * Constructor.
     */
    public SimpleParamStripper(Iterable<String> strippableParams, boolean ignoreCase) {
        super(ignoreCase);
        this.strippableParams = strippableParams;
    }

    /**
     * Return the parameters which should be stripped.
     */
    @Override
    protected final Iterable<String> getStrippableParams() {
        return strippableParams;
    }

    @Override
    protected Iterable<Pattern> getStrippablePatterns() {
        return null;
    }
}
