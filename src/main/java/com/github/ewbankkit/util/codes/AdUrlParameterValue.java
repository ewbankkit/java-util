/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.codes;

import java.util.Map;

import com.github.ewbankkit.util.MapBuilder;

/**
 * Ad URL parameter values.
 */
public final class AdUrlParameterValue {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:22 AdUrlParameterValue.java NSI";

	public static final String GOOGLE_KEYWORD_BROAD_MATCH = "b";
	public static final String GOOGLE_KEYWORD_EXACT_MATCH = "e";
	public static final String GOOGLE_KEYWORD_PHRASE_MATCH = "p";
	public static final String MICROSOFT_KEYWORD_BROAD_MATCH = "b";
	public static final String MICROSOFT_KEYWORD_EXACT_MATCH = "e";
	public static final String MICROSOFT_KEYWORD_PHRASE_MATCH = "p";

    public static final Map<String, String> GOOGLE_KEYWORD_MATCH_TYPE_MAP = new MapBuilder<String, String>().
                put(GOOGLE_KEYWORD_BROAD_MATCH, KeywordMatchType.BROAD).
                put(GOOGLE_KEYWORD_EXACT_MATCH, KeywordMatchType.EXACT).
                put(GOOGLE_KEYWORD_PHRASE_MATCH, KeywordMatchType.PHRASE).unmodifiableMap();
    public static final Map<String, String> MICROSOFT_KEYWORD_MATCH_TYPE_MAP = new MapBuilder<String, String>().
                put(MICROSOFT_KEYWORD_BROAD_MATCH, KeywordMatchType.BROAD).
                put(MICROSOFT_KEYWORD_EXACT_MATCH, KeywordMatchType.EXACT).
                put(MICROSOFT_KEYWORD_PHRASE_MATCH, KeywordMatchType.PHRASE).unmodifiableMap();

    public static final Map<Integer, Map<String, String>> KEYWORD_MATCH_TYPE_MAP = new MapBuilder<Integer, Map<String, String>>().
                put(Integer.valueOf(VendorId.GOOGLE), GOOGLE_KEYWORD_MATCH_TYPE_MAP).
                put(Integer.valueOf(VendorId.MICROSOFT), MICROSOFT_KEYWORD_MATCH_TYPE_MAP).unmodifiableMap();

	private AdUrlParameterValue() {
	    return;
	}
}
