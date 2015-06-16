/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import static com.netsol.adagent.util.beans.BaseData.toListOfPairs;

import com.netsol.adagent.util.beans.Pair;

public class SimpleParamAdder extends AbstractParamAdder {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:35 SimpleParamAdder.java NSI";

    private final Iterable<Pair<String, String>> addableParamsAndValues;

    /**
     * Constructor.
     */
    public SimpleParamAdder(String[][] addableParamsAndValues) {
        this(toListOfPairs(addableParamsAndValues));
    }

    /**
     * Constructor.
     */
    public SimpleParamAdder(Iterable<Pair<String, String>> addableParamsAndValues) {
        this.addableParamsAndValues = addableParamsAndValues;
    }

    /**
     * Return the parameters and values which should be added.
     */
    @Override
    protected final Iterable<Pair<String, String>> getAddableParamsAndValues() {
        return addableParamsAndValues;
    }
}
