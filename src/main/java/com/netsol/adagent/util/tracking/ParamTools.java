/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import static com.netsol.adagent.util.codes.AdUrlParameterName.GENERIC_CLICK_SOURCE_AD_GROUP_ID;
import static com.netsol.adagent.util.codes.AdUrlParameterName.GOOGLE_PARAMETERS;
import static com.netsol.adagent.util.codes.AdUrlParameterName.GOOGLE_SITELINK_COPY_TEMPLATE_PATTERN;
import static com.netsol.adagent.util.codes.AdUrlParameterName.GOOGLE_SITELINK_TEMPLATE;
import static com.netsol.adagent.util.codes.AdUrlParameterName.NS_AD_GROUP_ID;
import static com.netsol.adagent.util.codes.AdUrlParameterName.NS_VENDOR_ID;
import static com.netsol.adagent.util.codes.AdUrlParameterName.PARAMETERS_MAP;
import static com.netsol.adagent.util.codes.AdUrlParameterName.TEMPLATE_MAP;
import static com.netsol.adagent.util.codes.VendorId.GENERIC_CLICK_SOURCE;
import static com.netsol.adagent.util.codes.VendorId.GOOGLE;
import static com.netsol.adagent.util.codes.VendorId.MICROSOFT;
import static com.netsol.adagent.util.codes.VendorId.SUPERPAGES;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.netsol.adagent.util.MapBuilder;
import com.netsol.adagent.util.beans.Pair;

public final class ParamTools {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:35 ParamTools.java NSI";

    private static final ParamTools INSTANCE = new ParamTools();

    private final Map<Integer, ParamStripper> vendorIdToParamStripperMap = new MapBuilder<Integer, ParamStripper>().
                put(Integer.valueOf(GENERIC_CLICK_SOURCE),
                    new SimpleParamStripper(new String[] {GENERIC_CLICK_SOURCE_AD_GROUP_ID}, false)).
                put(Integer.valueOf(GOOGLE),
                    new AbstractAdVendorParamStripper(GOOGLE_PARAMETERS,
                                                      Collections.singleton(GOOGLE_SITELINK_COPY_TEMPLATE_PATTERN)) {}).
                put(newAdVendorPair(MICROSOFT)).
                put(Integer.valueOf(SUPERPAGES),
                    new SimpleParamStripper(new String[] {NS_AD_GROUP_ID, NS_VENDOR_ID}, false)).unmodifiableMap();
    private final Collection<ParamStripper> paramStrippers = Collections.unmodifiableCollection(vendorIdToParamStripperMap.values());

    /**
     * Return the parameter adder for the specified vendor.
     */
    public static ParamAdder getParamAdder(int vendorId, long nsAdGroupId) {
        if (vendorId == GENERIC_CLICK_SOURCE) {
            return new SimpleParamAdder(new String[][] {new String[] {GENERIC_CLICK_SOURCE_AD_GROUP_ID, Long.toString(nsAdGroupId)}});
        }
        
        if (vendorId == SUPERPAGES) {
            return new SimpleParamAdder(new String[][] {
                            new String[] {NS_AD_GROUP_ID, Long.toString(nsAdGroupId)},
                            new String[] {NS_VENDOR_ID, Integer.toString(vendorId)}});
        }
        
        Collection<Pair<String, String>> addableParamsAndValues = TEMPLATE_MAP.get(Integer.valueOf(vendorId));
        if (addableParamsAndValues == null) {
            return null;
        }
        
        return new AbstractAdVendorParamAdder(addableParamsAndValues, nsAdGroupId) {};
    }

    /**
     * Return the sitelinks parameter adder for the specified vendor.
     */
    public static ParamAdder getSitelinksParamAdder(int vendorId) {
        if (vendorId == GOOGLE) {
            return new SimpleParamAdder(GOOGLE_SITELINK_TEMPLATE);
        }
        return null;
    }

    /**
     * Return the parameter stripper for the specified vendor.
     */
    public static ParamStripper getParamStripper(int vendorId) {
        return ParamTools.INSTANCE.vendorIdToParamStripperMap.get(Integer.valueOf(vendorId));
    }

    /**
     * Return all the parameter strippers.
     */
    public static Collection<ParamStripper> getParamStrippers() {
        return ParamTools.INSTANCE.paramStrippers;
    }

    private static Pair<Integer, ? extends ParamStripper> newAdVendorPair(int vendorId) {
        Integer key = Integer.valueOf(vendorId);
        return Pair.from(key, new AbstractAdVendorParamStripper(PARAMETERS_MAP.get(key)) {});
    }
}
