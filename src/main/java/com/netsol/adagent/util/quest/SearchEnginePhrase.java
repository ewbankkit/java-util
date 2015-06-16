/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.quest;

import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.beans.BaseData;

/**
 * Search engine phrase. 
 */
public class SearchEnginePhrase extends BaseData {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:21 SearchEnginePhrase.java NSI";
    
    public static final String BUSINESS_CATEGORY_SOURCE = "BUSINESS CATEGORY";
    public static final String BUSINESS_NAME_SOURCE = "BUSINESS NAME";
    public static final String BUSINESS_PHONE_NUMBER_SOURCE = "BUSINESS PHONE NUMBER";
    public static final String BUSINESS_STREET_ADDRESS_SOURCE = "BUSINESS STREET ADDRESS";
    public static final String BUSINESS_ZIP_CODE_SOURCE = "BUSINESS ZIP CODE";
    public static final String DESCRIPTIVE_KEYWORD_SOURCE = "DESCRIPTIVE KEYWORD";
    public static final String META_TAG_KEYWORD_SOURCE = "META TAG KEYWORD";
    public static final String SITE_URL_SOURCE = "SITE URL";
    
    public static final String SEARCH_PHRASE_DELIMITER = ";";

    private String searchPhrase;
    private String searchPhraseSource;
    private String url;
    
    /**
     * Constructor.
     */
    public SearchEnginePhrase() {
        super();
        
        return;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public String getSearchPhrase() {
        return this.searchPhrase;
    }

    public void setSearchPhraseSource(String searchPhraseSource) {
        this.searchPhraseSource = searchPhraseSource;
    }

    public String getSearchPhraseSource() {
        return this.searchPhraseSource;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchEnginePhrase)) {
            return false;
        }
        SearchEnginePhrase rhs = (SearchEnginePhrase)o;
        return SearchEnginePhrase.searchPhrasesEqual(this, rhs) && SearchEnginePhrase.urlsEqual(this, rhs);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (this.searchPhrase != null) {
            result = 31 * result + this.searchPhrase.toLowerCase().hashCode();
        }
        String hostName = TrackingUtil.extractHostName(this.url);
        if (hostName != null) {
            result = 31 * result + hostName.hashCode();
        }
        
        return result;
    }
    
    /**
     * Are the search phrases equal? 
     */
    protected static boolean searchPhrasesEqual(SearchEnginePhrase sep1, SearchEnginePhrase sep2) {
        return BaseData.stringsEqualIgnoreCase(sep1.searchPhrase, sep2.searchPhrase);
    }
    
    /**
     * Are the URLs equal? 
     */
    protected static boolean urlsEqual(SearchEnginePhrase sep1, SearchEnginePhrase sep2) {
        return SearchEnginePhrase.stringsEqual(TrackingUtil.extractHostName(sep1.url), TrackingUtil.extractHostName(sep2.url));
    }
}
