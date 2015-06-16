/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.codes.AdUrlParameterName;

/* package-private */ abstract class AbstractAdVendorParamAdder extends SimpleParamAdder {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:31 AbstractAdVendorParamAdder.java NSI";

    /**
     * Constructor.
     */
    protected AbstractAdVendorParamAdder(Collection<Pair<String, String>> addableParamsAndValues, long nsAdGroupId) {
        super(getAllAddableParamsAndValues(addableParamsAndValues, nsAdGroupId));
    }

    /**
     * Return all parameters and values which should be added.
     */
    private static Collection<Pair<String, String>> getAllAddableParamsAndValues(Collection<Pair<String, String>> addableParamsAndValues, long nsAdGroupId) {
        Collection<Pair<String, String>> allAddableParamsAndValues = new ArrayList<Pair<String, String>>(addableParamsAndValues);
        allAddableParamsAndValues.add(Pair.from(AdUrlParameterName.NS_AD_GROUP_ID, Long.toString(nsAdGroupId)));
        return Collections.unmodifiableCollection(allAddableParamsAndValues);
    }
}
