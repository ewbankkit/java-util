/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

import static com.netsol.adagent.util.beans.BaseData.toListOfPairs;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import com.netsol.adagent.util.ListBuilder;
import com.netsol.adagent.util.MapBuilder;
import com.netsol.adagent.util.beans.Pair;

/**
 * Ad URL parameter names.
 */
public final class AdUrlParameterName {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:22 AdUrlParameterName.java NSI";

	public static final String GENERIC_CLICK_SOURCE_AD_GROUP_ID = "_nsag";
	public static final String GOOGLE_AD_ID = "creative";
	public static final String GOOGLE_AD_ID_TEMPLATE = "{creative}";
	public static final String GOOGLE_KEYWORD = "keywords";
	public static final String GOOGLE_KEYWORD_TEMPLATE = "{keyword}";
	public static final String GOOGLE_KEYWORD_MATCH_TYPE = "matchtype";
	public static final String GOOGLE_KEYWORD_MATCH_TYPE_TEMPLATE = "{matchtype}";
    public static final String MICROSOFT_AD_ID = "MSADID";
    public static final String MICROSOFT_AD_ID_TEMPLATE = "{AdId}";
    public static final String MICROSOFT_KEYWORD_ID = "MSKWID";
    public static final String MICROSOFT_KEYWORD_ID_TEMPLATE = "{OrderItemId}";
    public static final String MICROSOFT_KEYWORD_MATCH_TYPE = "MSKWMT";
    public static final String MICROSOFT_KEYWORD_MATCH_TYPE_TEMPLATE = "{MatchType}";
	public static final String NS_AD_GROUP_ID = "adGroup";
	// Used when the vendor has no distinguishing parameters (e.g. SuperPages).
	public static final String NS_VENDOR_ID = "vendor";

    public static final String GOOGLE_SITELINK_KEYWORD_MATCH_TYPE_TEMPLATE = "{copy:" + GOOGLE_KEYWORD_MATCH_TYPE + "}";
    public static final String GOOGLE_SITELINK_NS_AD_GROUP_ID_TEMPLATE = "{copy:" + NS_AD_GROUP_ID + "}";

    public static final Pattern GOOGLE_SITELINK_COPY_TEMPLATE_PATTERN = Pattern.compile("\\{copy:\\w*\\}"); // e.g. {copy:adGroup}
    public static final Collection<Pair<String, String>> GOOGLE_SITELINK_TEMPLATE = toListOfPairs(new String[] [] {
            new String[] {GOOGLE_AD_ID, GOOGLE_AD_ID_TEMPLATE},
            new String[] {GOOGLE_KEYWORD, GOOGLE_KEYWORD_TEMPLATE},
            new String[] {GOOGLE_SITELINK_KEYWORD_MATCH_TYPE_TEMPLATE, null},
            new String[] {GOOGLE_SITELINK_NS_AD_GROUP_ID_TEMPLATE, null}});

    private static final Collection<Pair<String, String>> GOOGLE_TEMPLATE = toListOfPairs(new String[][] {
            new String[] {GOOGLE_AD_ID, GOOGLE_AD_ID_TEMPLATE},
            new String[] {GOOGLE_KEYWORD, GOOGLE_KEYWORD_TEMPLATE},
            new String[] {GOOGLE_KEYWORD_MATCH_TYPE, GOOGLE_KEYWORD_MATCH_TYPE_TEMPLATE}});
    private static final Collection<Pair<String, String>> MICROSOFT_TEMPLATE = toListOfPairs(new String[][] {
            new String[] {MICROSOFT_AD_ID, MICROSOFT_AD_ID_TEMPLATE},
            new String[] {MICROSOFT_KEYWORD_ID, MICROSOFT_KEYWORD_ID_TEMPLATE},
            new String[] {MICROSOFT_KEYWORD_MATCH_TYPE, MICROSOFT_KEYWORD_MATCH_TYPE_TEMPLATE}});

    public static final Collection<String> GOOGLE_PARAMETERS = new ListBuilder<String>().
                add(GOOGLE_AD_ID).
                add(GOOGLE_KEYWORD).
                add(GOOGLE_KEYWORD_MATCH_TYPE).unmodifiableList();
    public static final Collection<String> MICROSOFT_PARAMETERS = new ListBuilder<String>().
                add(MICROSOFT_AD_ID).
                add(MICROSOFT_KEYWORD_ID).
                add(MICROSOFT_KEYWORD_MATCH_TYPE).unmodifiableList();

    public static final Collection<String> ALL_PARAMETERS = new ListBuilder<String>().
                addAll(GOOGLE_PARAMETERS).
                addAll(MICROSOFT_PARAMETERS).
                add(GENERIC_CLICK_SOURCE_AD_GROUP_ID).
                add(NS_AD_GROUP_ID).
                add(NS_VENDOR_ID).unmodifiableList();

    public static final Map<Integer, Collection<String>> PARAMETERS_MAP = new MapBuilder<Integer, Collection<String>>().
                put(Integer.valueOf(VendorId.GOOGLE), GOOGLE_PARAMETERS).
                put(Integer.valueOf(VendorId.MICROSOFT), MICROSOFT_PARAMETERS).unmodifiableMap();

    public static final Map<Integer, Collection<Pair<String, String>>> TEMPLATE_MAP = new MapBuilder<Integer, Collection<Pair<String, String>>>().
                put(Integer.valueOf(VendorId.GOOGLE), GOOGLE_TEMPLATE).
                put(Integer.valueOf(VendorId.MICROSOFT), MICROSOFT_TEMPLATE).unmodifiableMap();

	private AdUrlParameterName() {}
}
